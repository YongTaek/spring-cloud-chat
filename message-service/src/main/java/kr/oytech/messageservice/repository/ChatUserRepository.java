package kr.oytech.messageservice.repository;

import kr.oytech.messageservice.model.ChatUser;
import kr.oytech.messageservice.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ChatUserRepository extends ReactiveMongoRepository<ChatUser, Long> {

  Mono<ChatUser> findByUser(User user);
}
