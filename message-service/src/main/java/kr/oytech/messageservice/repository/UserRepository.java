package kr.oytech.messageservice.repository;

import kr.oytech.messageservice.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

}
