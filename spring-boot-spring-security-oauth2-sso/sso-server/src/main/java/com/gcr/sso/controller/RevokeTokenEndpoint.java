package com.gcr.sso.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author litz-a
 */
@FrameworkEndpoint
public class RevokeTokenEndpoint {

  @Autowired
  ConsumerTokenServices tokenServices;

  @DeleteMapping(value = "/oauth/token")
  @ResponseBody
  public void revokeToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    if (authorization != null && authorization.contains("Bearer")) {
      String tokenId = authorization.substring("Bearer".length() + 1);
      tokenServices.revokeToken(tokenId);
    }
  }

}