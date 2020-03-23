package kr.oytech.messageservice.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class ChatUser {

  @Id
  long id;

  @DBRef
  User user;

  @DBRef
  List<Chat> chatList;
}
