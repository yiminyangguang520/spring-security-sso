package cn.lee.sso.server;

import cn.lee.sso.config.CustomTokenEnhancer;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * @author litz-a
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfigJdbc extends AuthorizationServerConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private AuthenticationManager authenticationManager;

  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Value("classpath:schema.sql")
  private Resource schemaScript;

  @Value("classpath:data.sql")
  private Resource dataScript;


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

  @Bean
  public TokenEnhancer tokenEnhancer() {
    return new CustomTokenEnhancer();
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
        .tokenKeyAccess("permitAll()")
        .checkTokenAccess("isAuthenticated()");
  }

  @Override
  public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
    clients
        .jdbc(dataSource)
        .passwordEncoder(passwordEncoder)
//        .withClient("merryyou1")
//          .secret(new BCryptPasswordEncoder().encode("merryyousecrect1"))
//          .authorizedGrantTypes("authorization_code", "refresh_token")
//          .redirectUris("http://xxlssoclient1.com:8083/client1/login")
//          .scopes("all", "read", "write")
//          .autoApprove(true)
//        .and()
//          .withClient("merryyou2")
//          .secret(new BCryptPasswordEncoder().encode("merryyousecrect2"))
//          .authorizedGrantTypes("authorization_code", "refresh_token")
//          .redirectUris("http://xxlssoclient2.com:8084/client2/login")
//          .scopes("all", "read", "write")
//          .autoApprove(true);

//    .withClientDetails(clientDetailsService())
    ;
  }

  @Override
  public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
    tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer()));
    endpoints.tokenStore(tokenStore())
        .tokenEnhancer(tokenEnhancerChain)
        .authorizationCodeServices(authorizationCodeServices(dataSource))
        .approvalStore(approvalStore())
        .userApprovalHandler(userApprovalHandler())
        .authenticationManager(authenticationManager);
  }
}
