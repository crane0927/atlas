/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.security;

import com.atlas.common.feature.security.validator.TokenValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 安全上下文 Web 自动配置
 *
 * <p>当存在 {@link TokenValidator} Bean 时注册 SecurityContextImpl 与 SecurityContextFilter。由 atlas-auth
 * 等模块通过 SecurityConfig 将 SecurityContext 注册到 SecurityContextHolder。
 *
 * @author Atlas
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnBean(TokenValidator.class)
public class SecurityContextWebAutoConfiguration {

  @Bean
  public SecurityContextImpl securityContextImpl() {
    return new SecurityContextImpl();
  }

  @Bean
  public FilterRegistrationBean<SecurityContextFilter> securityContextFilterRegistration(
      TokenValidator tokenValidator, SecurityContextImpl securityContextImpl) {
    FilterRegistrationBean<SecurityContextFilter> registration =
        new FilterRegistrationBean<>(
            new SecurityContextFilter(tokenValidator, securityContextImpl));
    registration.addUrlPatterns("/*");
    registration.setName("securityContextFilter");
    registration.setOrder(100);
    return registration;
  }
}
