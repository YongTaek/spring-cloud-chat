package kr.oytech.userservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class User {

  @Id
  String id;

  Object oauthId;
  String name;

}
