package kr.oytech.messageservice.repository;

import kr.oytech.messageservice.model.Chat;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {

}
