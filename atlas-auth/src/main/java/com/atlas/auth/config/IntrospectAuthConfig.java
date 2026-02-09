/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.auth.config;

import com.atlas.auth.filter.IntrospectAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Introspection 接口服务间认证配置
 *
 * <p>注册 {@link IntrospectAuthFilter}，仅对 POST /api/v1/auth/introspect 生效。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Configuration
public class IntrospectAuthConfig {

  @Bean
  public FilterRegistrationBean<IntrospectAuthFilter> introspectAuthFilterRegistration(
      AuthProperties authProperties, ObjectMapper objectMapper) {
    FilterRegistrationBean<IntrospectAuthFilter> registration =
        new FilterRegistrationBean<>(new IntrospectAuthFilter(authProperties, objectMapper));
    registration.addUrlPatterns("/api/v1/auth/introspect");
    registration.setName("introspectAuthFilter");
    registration.setOrder(1);
    return registration;
  }
}
