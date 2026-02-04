/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.auth.config;

import com.atlas.auth.context.AuthSecurityContextHolder;
import com.atlas.auth.context.SecurityContextImpl;
import com.atlas.auth.filter.SecurityContextFilter;
import com.atlas.common.feature.security.context.SecurityContextHolder;
import com.atlas.common.feature.security.provider.CurrentUserProvider;
import com.atlas.common.feature.security.provider.SecurityContextCurrentUserProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 安全配置类
 *
 * <p>配置安全相关的 Bean 和过滤器。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>注册 `SecurityContextImpl` Bean
 *   <li>注册 `SecurityContextFilter` 过滤器
 *   <li>设置 `SecurityContextHolder` 的实现
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Configuration("authSecurityConfig")
public class SecurityConfig {

  /**
   * 创建 SecurityContextImpl Bean
   *
   * <p>使用 ThreadLocal 存储用户信息的安全上下文实现。
   *
   * @return SecurityContextImpl 实例
   */
  @Bean
  public SecurityContextImpl securityContext() {
    return new SecurityContextImpl();
  }

  /**
   * 注册 SecurityContextFilter 过滤器
   *
   * <p>将 `SecurityContextFilter` 注册为 Spring Filter，用于从请求头提取 Token 并设置安全上下文。
   *
   * @param securityContextFilter SecurityContextFilter 实例
   * @return FilterRegistrationBean
   */
  @Bean
  public FilterRegistrationBean<SecurityContextFilter> securityContextFilterRegistration(
      SecurityContextFilter securityContextFilter) {
    FilterRegistrationBean<SecurityContextFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(securityContextFilter);
    registration.addUrlPatterns("/*");
    registration.setName("securityContextFilter");
    registration.setOrder(100);
    return registration;
  }

  /**
   * 初始化 AuthSecurityContextHolder
   *
   * <p>设置 `AuthSecurityContextHolder` 的上下文实例，使下游服务可以通过静态方法获取安全上下文。
   *
   * @param securityContext SecurityContextImpl 实例
   * @return AuthSecurityContextHolder 实例
   */
  @Bean
  public AuthSecurityContextHolder authSecurityContextHolder(SecurityContextImpl securityContext) {
    // 设置静态上下文实例（供直接使用 AuthSecurityContextHolder 的代码）
    AuthSecurityContextHolder.setContextInstance(securityContext);
    // 注册到 common SecurityContextHolder，使 AuditMetaObjectHandler 等通过 getLoginUser() 能拿到当前用户
    SecurityContextHolder.setContextProvider(AuthSecurityContextHolder::getContext);
    return new AuthSecurityContextHolder();
  }

  /**
   * 注册当前用户提供者，供审计等组件通过注入获取当前用户名（参考 Oneself 设计）。
   *
   * @return CurrentUserProvider 实例
   */
  @Bean
  public CurrentUserProvider currentUserProvider() {
    return new SecurityContextCurrentUserProvider();
  }
}
