package kr.oytech.messageservice.handler;

import java.util.LinkedList;
import java.util.Map;
import kr.oytech.messageservice.model.Chat;
import kr.oytech.messageservice.model.ChatUser;
import kr.oytech.messageservice.repository.ChatRepository;
import kr.oytech.messageservice.repository.ChatUserRepository;
import kr.oytech.messageservice.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ChatHandler {

  private ChatRepository chatRepository;
  private UserRepository userRepository;
  private ChatUserRepository chatUserRepository;

  @Autowired
  public ChatHandler(ChatRepository chatRepository,
      UserRepository userRepository, ChatUserRepository chatUserRepository) {
    this.chatRepository = chatRepository;
    this.userRepository = userRepository;
    this.chatUserRepository = chatUserRepository;

  }

  public Mono<ServerResponse> addUserToChat(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(AddUserToChatRequest.class)
        .flatMap(param -> Mono.zip(chatRepository.findById(param.getChatId()),
            userRepository.findById(param.getUserId())))
        .flatMap(t -> Mono.zip(Mono.just(t.getT1()), chatUserRepository.findByUser(t.getT2())
            .switchIfEmpty(Mono.just(ChatUser.builder()
                .user(t.getT2())
                .chatList(new LinkedList<>())
                .build())
            )))
        .flatMap(tuple2 -> {
          Chat chat = tuple2.getT1();
          ChatUser chatUser = tuple2.getT2();

          if (chatUser.getChatList() == null) {
            chatUser.setChatList(new LinkedList<>());
          }
          chatUser.getChatList().add(chat);
          return chatUserRepository.save(chatUser);
        })
        .flatMap(chatUser -> ServerResponse.ok().bodyValue(Map.of("success", true)))
        .switchIfEmpty(ServerResponse.ok().bodyValue(false))
        .onErrorResume(throwable -> {
          System.out.println(throwable.getMessage());
          return ServerResponse.ok().bodyValue(false);
        })
        .log();
  }

  public Mono<ServerResponse> createNewChat(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(CreateChatRequest.class)
        .map(param -> Chat.builder().name(param.getName()).build())
        .flatMap(chat -> chatRepository.save(chat))
        .map(chat -> {
          System.out.println(chat.getId());
          return chat;
        })
        .flatMap(
            chat -> ServerResponse.ok().bodyValue(Map.of("success", true, "chatId", chat.getId())));
  }

  @Data
  private static class AddUserToChatRequest {

    private String chatId;
    private String userId;
  }

  @Data
  private static class CreateChatRequest {

    private String name;
  }
}
