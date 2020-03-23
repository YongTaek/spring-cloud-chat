package kr.oytech.messageservice.repository;

import kr.oytech.messageservice.model.Chat;
import kr.oytech.messageservice.model.Message;
import kr.oytech.messageservice.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

  Flux<Message> findAllByUser(User user);

  Flux<Message> findAllByChat(Chat chat);
}
