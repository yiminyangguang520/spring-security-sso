package com.gcr.sso.security;

import com.gcr.sso.constant.Domain;
import com.gcr.sso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

/**
 * @author litz-a
 */
@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserService userService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    String result = Constant.ERROR;
    try {
      result = userService.accountAuthenticate(username, password);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (!Constant.PASS.equals(result)) {
      log.error("Authentication failed for user = " + username);
      throw new BadCredentialsException("Authentication failed for user = " + username);
    }

    log.info("Succesful Authentication with user = " + username);
    return new UsernamePasswordAuthenticationToken(username, password,
        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}