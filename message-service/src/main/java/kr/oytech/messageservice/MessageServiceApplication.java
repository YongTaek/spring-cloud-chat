package kr.oytech.messageservice;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import kr.oytech.messageservice.handler.ChatHandler;
import kr.oytech.messageservice.handler.MessageHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@EnableDiscoveryClient
@SpringBootApplication
public class MessageServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MessageServiceApplication.class, args);
  }

  @Bean
  public RouterFunction<ServerResponse> routes(MessageHandler messageHandler,
      ChatHandler chatHandler) {
    return route()
        .GET("/", request -> ServerResponse.ok().bodyValue("messageService"))
        .POST("/messages", messageHandler::sendMessage)
        .POST("/chats", chatHandler::createNewChat)
        .POST("/users/chats", chatHandler::addUserToChat)
        .build();
  }
}
