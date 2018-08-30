package cn.lee.sso.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

/**
 * @author litz-a
 */
public class CustomClientDetailService implements ClientDetailsService {

  @Autowired
  private DataSource dataSource;
  /**
   * Load a client by the client id. This method must not return null.
   *
   * @param clientId The client id.
   * @return The client details (never null).
   * @throws ClientRegistrationException If the client account is locked, expired, disabled, or invalid for any other reason.
   */
  @Override
  public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
    JdbcClientDetailsService jdbcClientDetailsService= new JdbcClientDetailsService(dataSource);
    ClientDetails clientDetails = jdbcClientDetailsService.loadClientByClientId(clientId);
    return clientDetails;
  }
}
