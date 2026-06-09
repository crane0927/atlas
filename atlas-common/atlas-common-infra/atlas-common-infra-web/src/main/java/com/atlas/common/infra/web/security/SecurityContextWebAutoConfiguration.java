/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.security;

import com.atlas.common.feature.security.context.SecurityContext;
import com.atlas.common.feature.security.provider.CurrentUserProvider;
import com.atlas.common.feature.security.provider.SecurityContextCurrentUserProvider;
import com.atlas.common.feature.security.validator.TokenValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 安全上下文 Web 自动配置
 *
 * <p>注册 SecurityContextImpl、SecurityContextFilter，并将 SecurityContext 注册到 SecurityContextHolder，使仅依赖
 * common 的业务服务（不依赖 atlas-auth）也能通过 Gateway 下传请求头获得 当前用户与审计填充。当未提供 {@link TokenValidator} 时使用仅返回
 * null 的默认实现，仅从请求头解析用户。
 *
 * @author Atlas
 * @since 1.0.0
 */
@AutoConfiguration
public class SecurityContextWebAutoConfiguration {

  @Bean
  public SecurityContextImpl securityContextImpl() {
    return new SecurityContextImpl();
  }

  /** 默认 TokenValidator：不校验 Bearer，仅依赖 Gateway 下传请求头时使用。 */
  @Bean
  @ConditionalOnMissingBean(TokenValidator.class)
  public TokenValidator noOpTokenValidator() {
    return token -> null;
  }

  @Bean
  public SecurityContextHolderConfigurer securityContextHolderConfigurer(
      SecurityContext securityContext) {
    return new SecurityContextHolderConfigurer(securityContext);
  }

  @Bean
  @ConditionalOnMissingBean(CurrentUserProvider.class)
  public CurrentUserProvider currentUserProvider() {
    return new SecurityContextCurrentUserProvider();
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
