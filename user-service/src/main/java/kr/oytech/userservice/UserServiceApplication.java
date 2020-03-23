package kr.oytech.userservice;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import kr.oytech.userservice.handler.UserHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@EnableDiscoveryClient
@SpringBootApplication
public class UserServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }

  @Bean
  public RouterFunction<ServerResponse> routes(UserHandler userHandler) {
    return route()
        .GET("/users", userHandler::getUsers)
        .POST("/users", userHandler::addUser)
        .GET("/", serverRequest -> ServerResponse.ok().bodyValue("userService"))
        .build();
  }

}
