package kr.oytech.userservice.handler;

import java.util.Map;
import kr.oytech.userservice.model.User;
import kr.oytech.userservice.repository.UserRepository;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

  private UserRepository userRepository;

  @Autowired
  public UserHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Mono<ServerResponse> getUsers(ServerRequest request) {
    return userRepository.findAll().collectList()
        .flatMap(users -> ServerResponse.ok().bodyValue(users));
  }

  public Mono<ServerResponse> addUser(ServerRequest request) {
    return request.bodyToMono(AddUserRequest.class)
        .map(parameter -> User.builder()
            .oauthId(parameter.getOauthId())
            .name(parameter.getName())
            .build())
        .flatMap(user -> userRepository.findByOauthId(user.getOauthId())
            .flatMap(alreadyUser -> ServerResponse.ok()
                .header("Content-Type", "application/json")
                .bodyValue(Map.of("success", true, "message", "already exist", "userId", alreadyUser.getId())))
            .switchIfEmpty(userRepository.save(user)
                .flatMap(newUser -> ServerResponse.ok()
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of("success", true, "userId", newUser.getId()))))
        );

  }

  @Data
  private static class AddUserRequest {

    private Object oauthId;
    private String name;
  }

  @Builder
  private static class AddUserResponse {

    private boolean success;
  }

}
