package cn.lee.soo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;


/**
 * @author min
 */
@EnableOAuth2Sso
@SpringBootApplication
public class SsoClient2Application {

  public static void main(String[] args) {
    SpringApplication.run(SsoClient2Application.class, args);
  }
}
