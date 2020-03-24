package kr.oytech.authenticationservice.configuration;

import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

  @Bean
  public Decoder decoder() {
    return new GsonDecoder();
  }

  @Bean
  public Encoder encoder() {
    return new GsonEncoder();
  }

}
