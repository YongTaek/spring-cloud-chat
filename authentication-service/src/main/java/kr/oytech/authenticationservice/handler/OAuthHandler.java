package kr.oytech.authenticationservice.handler;

import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import kr.oytech.authenticationservice.configuration.GithubOauthConfig;
import kr.oytech.authenticationservice.model.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class OAuthHandler {

  private WebClient githubClient = WebClient.builder()
      .baseUrl("https://github.com")
      .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
      .build();

  private WebClient githubAPIClient = WebClient.builder()
      .baseUrl("https://api.github.com")
      .defaultHeader("Accept", "application/vnd.github.v3+json")
      .build();

  private final Key key;
  private GithubOauthConfig configProps;

  private UserServiceClient userServiceClient;

  @Autowired
  public OAuthHandler(GithubOauthConfig configProps, @Value("${jwt.secretKey}") String secretKey) {
    this.configProps = configProps;

    this.userServiceClient = Feign.builder()
        .encoder(new GsonEncoder())
        .decoder(new GsonDecoder())
        .target(UserServiceClient.class, "http://localhost:8765/users");
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public Mono<ServerResponse> getAccessTokenForGithub(ServerRequest request) {
    String code = request.queryParam("code").orElseThrow();
    String clientId = configProps.getGithub().getClientId();
    String clientSecret = configProps.getGithub().getClientSecret();

    return githubClient.post()
        .uri("/login/oauth/access_token")
        .body(BodyInserters.fromFormData("client_id", clientId)
            .with("client_secret", clientSecret)
            .with("code", code))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .flatMap(r -> r.bodyToMono(HashMap.class))
        .map(t -> (String) t.get("access_token"))
        .flatMap(accessToken -> githubAPIClient.get()
            .uri("/user").header("Authorization", "token " + accessToken)
            .exchange()
            .flatMap(r -> r.bodyToMono(HashMap.class))
            .map(t1 -> {
              int oauthId = (int) t1.get("id");
              String name = (String) t1.get("name");
              return User.builder()
                  .githubId(oauthId)
                  .name(name)
                  .build();
            })
            .map(user -> {
              AddUserRequest params = AddUserRequest.builder().name(user.getName())
                  .oauthId(user.getGithubId()).build();
              Map<String, Object> response = userServiceClient.addUser(params);
              if ((boolean) response.get("success")) {
                String userId = (String) response.get("userId");
                String jwtToken = Jwts.builder()
                    .setIssuedAt(new Date())
                    .setIssuer(userId)
                    .claim("userId", userId)
                    .signWith(key).compact();
                return Map.of("success", true, "jwt", jwtToken);
              } else {
                return Map.of("success", false);
              }
            }))
        .flatMap(response -> ServerResponse.ok().bodyValue(response));

  }

  @FeignClient(value = "userService")
  public interface UserServiceClient {

    @RequestLine("POST /users")
    @Headers({
        "Accept: application/json",
        "Content-Type: application/json"
    })
    Map<String, Object> addUser(AddUserRequest addUserRequest);

  }

  @Data
  @Builder
  private static class AddUserRequest {

    public String name;
    public Object oauthId;

  }
}
