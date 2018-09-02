package com.gcr.sso.config.server;

import com.gcr.sso.security.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author litz-a
 */
@Order(1)
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private LogoutSuccessHandler logoutSuccessHandler;

  @Autowired
  private CustomAuthenticationProvider customAuthenticationProvider;

  @Bean
  @Override
  public AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        // 跨域支持
        .cors()
        .and()
        // 关闭csrf
        .csrf().disable()
        //登录页面url,配置登录成功后调用的方法
        .formLogin()
          .loginPage("/authentication/require")
          .loginProcessingUrl("/authentication/form")
          .failureUrl("/authentication/require?error=1")
        .and()
          .authorizeRequests()
          // 配置这些链接无需验证
          .antMatchers("/authentication/require",
              "/authentication/form",
              "/**/*.js",
              "/**/*.css",
              "/**/*.jpg",
              "/**/*.png",
              "/**/*.woff2",
              "/oauth/*",
              "/login",
              "/perform_logout",
              "/webjars/**").permitAll()
          .antMatchers("/oauth/token/revokeById/**").permitAll()
          .antMatchers("/tokens/**").permitAll()
          // 除以上路径都需要验证
          .anyRequest().authenticated()
        .and()
          // 注销登录 --> 默认支持销毁session并且清空配置的rememberMe()认证 跳转登录页 或配置的注销成功页面
          .logout()
          .logoutUrl("/perform_logout")
          .deleteCookies("JSESSIONID")
          .invalidateHttpSession(false)
          .clearAuthentication(true)
          .logoutSuccessHandler(logoutSuccessHandler)
        .and()
          //当用户进行重复登录时,强制销毁前一个登录用户,配置此应用必须添加Listener HttpSessionEventPublisher
          .sessionManagement()
          .maximumSessions(1)
          .expiredUrl("/authentication/require")
         ;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(customAuthenticationProvider);
  }
}
