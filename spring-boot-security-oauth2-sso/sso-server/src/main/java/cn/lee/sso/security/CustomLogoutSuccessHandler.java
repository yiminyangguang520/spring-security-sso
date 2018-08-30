package cn.lee.sso.security;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

/**
 * @author litz-a
 */
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

  public CustomLogoutSuccessHandler() {
    super();
  }

  @Override
  public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
      throws IOException, ServletException {
    final String refererUrl = request.getHeader("Referer");

    //    super.onLogoutSuccess(request, response, authentication);

    new SecurityContextLogoutHandler().logout(request, null, null);
    try {
      //sending back to client app
      response.sendRedirect(request.getHeader("referer"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}