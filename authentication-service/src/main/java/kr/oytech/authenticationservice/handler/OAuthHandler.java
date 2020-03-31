package kr.oytech.authenticationservice.handler;

import feign.Headers;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import kr.oytech.authenticationservice.configuration.GithubOauthConfig;
import kr.oytech.authenticationservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

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

  private final WebClient.Builder loadBalancedWebClientBuilder;

  @Autowired
  public OAuthHandler(GithubOauthConfig configProps, @Value("${jwt.secretKey}") String secretKey,
      WebClient.Builder webClientBuilder) {
    this.configProps = configProps;
    this.loadBalancedWebClientBuilder = webClientBuilder;
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
        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM)
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
            }))
        .flatMap(user -> {
          AddUserRequest params = AddUserRequest.builder().name(user.getName())
              .oauthId(user.getGithubId()).build();
          return loadBalancedWebClientBuilder.build().post().uri("http://user-service/users")
              .bodyValue(params).exchange()
              .flatMap(response -> response.bodyToMono(AddUserResponse.class))
              .map(response -> {
                if (response.isSuccess()) {
                  String userId = response.getUserId();
                  String jwtToken = Jwts.builder()
                      .setIssuedAt(new Date())
                      .setIssuer(userId)
                      .claim("userId", userId)
                      .signWith(key).compact();
                  return Map.of("success", true, "jwt", jwtToken);
                } else {
                  return Map.of("success", false);
                }
              });
        })
        .flatMap(response -> ServerResponse.ok().bodyValue(response))
        .switchIfEmpty(ServerResponse.ok().bodyValue(Map.of("success", false)))
        .onErrorResume((throwable) -> {
          System.out.println(throwable.getMessage());
          return ServerResponse.ok().bodyValue(Map.of("success", false));
        });

  }


  @FeignClient(name = "userService")
  public interface UserServiceClient {

    //    @RequestLine("POST /users")
    @RequestMapping(value = "/users", method = RequestMethod.POST, consumes = "application/json")
    @Headers({
        "Accept: application/json",
        "Content-Type: application/json"
    })
    Map<String, Object> addUser(AddUserRequest addUserRequest);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getName();

  }

  @Data
  @Builder
  private static class AddUserRequest {

    public String name;
    public Object oauthId;

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  private static class AddUserResponse {

    private boolean success;
    @Nullable
    private String userId;
  }
}
