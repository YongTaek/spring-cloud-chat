package kr.oytech.messageservice.service;

import kr.oytech.messageservice.model.Message;
import kr.oytech.messageservice.repository.ChatUserRepository;
import kr.oytech.messageservice.repository.MessageRepository;
import kr.oytech.messageservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageService {

  private MessageRepository messageRepository;
  private UserRepository userRepository;
  private ChatUserRepository chatUserRepository;

  @Autowired
  public MessageService(MessageRepository messageRepository,
      UserRepository userRepository, ChatUserRepository chatUserRepository) {
    this.messageRepository = messageRepository;
    this.userRepository = userRepository;
    this.chatUserRepository = chatUserRepository;
  }

  public Mono<Message> sendMessage(String userId, String chatId, String content) {
    return userRepository.findById(userId)
        .flatMap(user ->
            chatUserRepository.findByUser(user)
                .map(chatUser -> chatUser.getChatList().stream()
                    .map(chat -> {
                      System.out.println(chat.getId());
                      return chat;
                    })
                    .filter(chat -> chat.getId().equals(chatId))
                    .map(chat -> Message.builder()
                        .chat(chat)
                        .user(user)
                        .content(content)
                        .build())
                    .findFirst().orElse(Message.empty())))
        .flatMap(message -> messageRepository.save(message))
        .switchIfEmpty(Mono.just(Message.empty()));
  }

}
