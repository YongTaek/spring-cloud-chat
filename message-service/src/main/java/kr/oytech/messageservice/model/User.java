package kr.oytech.messageservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class User {

  @Id
  private String id;

}
