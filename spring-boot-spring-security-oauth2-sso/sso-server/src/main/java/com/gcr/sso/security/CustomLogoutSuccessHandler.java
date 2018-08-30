package com.gcr.sso.security;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * @author litz-a
 */
@Slf4j
@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

  @Autowired
  private TokenStore tokenStore;

  @Autowired
  private AuthorizationServerTokenServices authorizationServerTokenServices;


  public CustomLogoutSuccessHandler() {
    super();
  }

  @Override
  public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
      throws IOException, ServletException {
    Enumeration<String> headers = request.getHeaderNames();
    while (headers.hasMoreElements()) {
      String header = headers.nextElement();
      System.out.println("=============================" + header + "-->" + request.getHeader(header));
      request.getHeader(header);
    }

    String token = request.getHeader("bearer ");
    if (token != null && token.startsWith("authorization")) {
      OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token.split(" ")[1]);
      if (oAuth2AccessToken != null) {
        tokenStore.removeAccessToken(oAuth2AccessToken);
      }
    }


    final String refererUrl = request.getHeader("Referer");

    log.info("Logout Sucessfull with Principal: " + authentication.getName());
    response.setStatus(HttpServletResponse.SC_OK);

    // super.onLogoutSuccess(request, response, authentication);

    new SecurityContextLogoutHandler().logout(request, null, null);
    //sending back to client app
    response.sendRedirect(request.getHeader("referer"));

  }

}