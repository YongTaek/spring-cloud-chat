package kr.oytech.authenticationservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {
    "classpath:/github.yaml"
})
@ConfigurationProperties(prefix = "oauth")
public class GithubOauthConfig {

  private Github github;

  public Github getGithub() {
    return github;
  }

  public GithubOauthConfig setGithub(Github github) {
    this.github = github;
    return this;
  }

  public static class Github {

    private String clientId, clientSecret;

    public String getClientId() {
      return clientId;
    }

    public Github setClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public String getClientSecret() {
      return clientSecret;
    }

    public Github setClientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
      return this;
    }
  }

}
