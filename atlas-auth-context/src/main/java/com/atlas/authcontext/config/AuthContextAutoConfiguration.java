/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.authcontext.config;

import com.atlas.authcontext.context.SecurityContextImpl;
import com.atlas.authcontext.filter.SecurityContextFilter;
import com.atlas.authcontext.validator.TokenValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 认证上下文自动配置（方案 B）
 *
 * <p>当存在 {@link TokenValidator} Bean 时注册 SecurityContextImpl 与 SecurityContextFilter。
 * setContextProvider 由 atlas-auth 的 SecurityConfig 调用（注入本模块的 SecurityContextImpl）。
 *
 * @author Atlas
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnBean(TokenValidator.class)
public class AuthContextAutoConfiguration {

  @Bean
  public SecurityContextImpl authContextSecurityContext() {
    return new SecurityContextImpl();
  }

  @Bean
  public FilterRegistrationBean<SecurityContextFilter> authContextFilterRegistration(
      TokenValidator tokenValidator, SecurityContextImpl authContextSecurityContext) {
    FilterRegistrationBean<SecurityContextFilter> registration =
        new FilterRegistrationBean<>(
            new SecurityContextFilter(tokenValidator, authContextSecurityContext));
    registration.addUrlPatterns("/*");
    registration.setName("securityContextFilter");
    registration.setOrder(100);
    return registration;
  }
}
