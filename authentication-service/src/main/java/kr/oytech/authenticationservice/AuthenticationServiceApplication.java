package kr.oytech.authenticationservice;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import kr.oytech.authenticationservice.handler.OAuthHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "kr.oytech.authenticationservice.handler")
@SpringBootApplication
public class AuthenticationServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthenticationServiceApplication.class, args);
  }

  @Bean
  RouterFunction<ServerResponse> routes(OAuthHandler oAuthHandler) {
    return route()
        .GET("/users", oAuthHandler::getAccessTokenForGithub)
        .build();
  }

}
