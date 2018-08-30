package com.gcr.sso.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author litz-a
 */
@RestController
public class TokenController {

  @Autowired
  private ConsumerTokenServices tokenServices;

  @Autowired
  private TokenStore tokenStore;

  @PostMapping(value = "/oauth/token/revokeById/{tokenId}")
  public void revokeToken(HttpServletRequest request, @PathVariable String tokenId) {
    tokenServices.revokeToken(tokenId);
  }

  @GetMapping(value = "/tokens")
  public List<String> getTokens() {
    List<String> tokenValues = new ArrayList<>();
    Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId("merryyou1");
    if (tokens != null) {
      for (OAuth2AccessToken token : tokens) {
        tokenValues.add(token.getValue());
      }
    }
    return tokenValues;
  }

  @PostMapping(value = "/tokens/revokeRefreshToken/{tokenId:.*}")
  public String revokeRefreshToken(@PathVariable String tokenId) {
    if (tokenStore instanceof JdbcTokenStore) {
      ((JdbcTokenStore) tokenStore).removeRefreshToken(tokenId);
    }
    return tokenId;
  }

}