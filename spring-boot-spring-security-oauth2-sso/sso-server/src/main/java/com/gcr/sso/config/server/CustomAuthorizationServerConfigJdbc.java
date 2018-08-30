package com.gcr.sso.config.server;

import com.gcr.sso.config.token.CustomTokenEnhancer;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * @author litz-a
 */
@Configuration
@EnableAuthorizationServer
public class CustomAuthorizationServerConfigJdbc extends AuthorizationServerConfigurerAdapter {

  @Value("classpath:schema.sql")
  private Resource schemaScript;

  @Value("classpath:data.sql")
  private Resource dataScript;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private CustomTokenEnhancer customTokenEnhancer;

  @Autowired
  private AuthenticationManager authenticationManager;


  @Bean
  public ClientDetailsService clientDetailsService() {
    return new JdbcClientDetailsService(dataSource);
  }

  @Bean
  public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
    return new JdbcAuthorizationCodeServices(dataSource);
  }

  @Bean
  public OAuth2RequestFactory oAuth2RequestFactory() {
    return new DefaultOAuth2RequestFactory(clientDetailsService());
  }

  @Bean
  public UserApprovalHandler userApprovalHandler() {
    ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
    userApprovalHandler.setApprovalStore(new JdbcApprovalStore(dataSource));
    userApprovalHandler.setClientDetailsService(clientDetailsService());
    userApprovalHandler.setRequestFactory(oAuth2RequestFactory());
    return userApprovalHandler;
  }

  @Bean
  public ApprovalStore approvalStore() {
    return new JdbcApprovalStore(dataSource);
  }

  @Bean
  @Primary
  public DefaultTokenServices tokenServices() {
    final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(tokenStore());
    defaultTokenServices.setSupportRefreshToken(true);
    return defaultTokenServices;
  }

  /**
   * JDBC token store configuration
   * @param dataSource
   * @return
   */
  @Bean
  public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
    final DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(databasePopulator());
    return initializer;
  }

  private DatabasePopulator databasePopulator() {
    final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(schemaScript);
    populator.addScript(dataScript);
    return populator;
  }

  @Bean
  public TokenStore tokenStore() {
    return new JdbcTokenStore(dataSource);
  }

  @Override
  public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    oauthServer.passwordEncoder(passwordEncoder)
        //url:/oauth/token_key
        .tokenKeyAccess("permitAll()")
        //url:/oauth/check_token allow check token
        .checkTokenAccess("isAuthenticated()");
  }

  @Override
  public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
    // 方式一
    clients
        .jdbc(dataSource)
        .passwordEncoder(passwordEncoder)
    // 方式二
    // clients.withClientDetails(clientDetailsService())
    ;
  }

  @Override
  public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
    tokenEnhancerChain.setTokenEnhancers(Arrays.asList(customTokenEnhancer));
    endpoints
        .tokenStore(tokenStore())
        .tokenEnhancer(tokenEnhancerChain)
        .authorizationCodeServices(authorizationCodeServices(dataSource))
        .approvalStore(approvalStore())
        .userApprovalHandler(userApprovalHandler())
        .authenticationManager(authenticationManager);
  }
}
