package kr.oytech.messageservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class Message {

  @Id
  private String id;

  @DBRef
  private User user;

  @DBRef
  private Chat chat;

  private String content;

  public static Message empty() {
    return Message.builder().id("-1").build();
  }

  public static boolean isEmpty(Message message) {
    return message.getId().equals("-1");
  }
}
