package cn.lee.soo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;


/**
 * @author litz-a
 */
@SpringBootApplication
public class SsoClient2Application {

  @Bean
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }

  public static void main(String[] args) {
    SpringApplication.run(SsoClient2Application.class, args);
  }
}
