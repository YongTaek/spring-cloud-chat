package kr.oytech.messageservice.handler;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Map;
import kr.oytech.messageservice.model.Message;
import kr.oytech.messageservice.service.MessageService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MessageHandler {

  private MessageService messageService;

  private final Key key;

  @Autowired
  public MessageHandler(MessageService messageService,
      @Value("${jwt.secretKey}") String secretKey) {
    this.messageService = messageService;
    key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public Mono<ServerResponse> sendMessage(ServerRequest request) {
    return request.bodyToMono(SendMessageRequest.class)
        .map(parameter -> {
          Jwt jwt = Jwts.parserBuilder().setSigningKey(key).build()
              .parse(parameter.getToken());
          String userId = (String) ((Claims) jwt.getBody()).get("userId");
          return Map.of("userId", userId, "chatId", parameter.getChatId(), "content",
              parameter.getContent());
        })
        .flatMap(parameter -> messageService
            .sendMessage(parameter.get("userId"), parameter.get("chatId"),
                parameter.get("content")))
        .flatMap(message -> {
          if (Message.isEmpty(message)) {
            return ServerResponse.ok().bodyValue(Map.of("success", false));
          } else {
            return ServerResponse.ok().bodyValue(
                Map.of("success", true, "messageId", message.getId(), "messageContent",
                    message.getContent()));
          }
        });
  }

  @Data
  private static class SendMessageRequest {

    private String token;
    private String chatId;
    private String content;
  }

}
