package kr.oytech.userservice.repository;

import kr.oytech.userservice.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

  Mono<User> findByOauthId(Object oauthId);
}
