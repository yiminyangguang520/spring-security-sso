package com.gcr.sso.config.token;

import java.util.Collections;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

/**
 * @author litz-a
 */
@Component
public class CustomTokenEnhancer implements TokenEnhancer {

  @Override
  public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(Collections.singletonMap("organization", "Glodon-" + authentication.getName()));
    return accessToken;
  }
}