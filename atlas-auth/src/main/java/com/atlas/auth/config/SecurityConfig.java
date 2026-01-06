/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.config;

import com.atlas.auth.context.AuthSecurityContextHolder;
import com.atlas.auth.context.SecurityContextImpl;
import com.atlas.auth.filter.SecurityContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

/**
 * 安全配置类
 *
 * <p>配置安全相关的 Bean 和过滤器。
 *
 * <p>功能特性：
 * <ul>
 *   <li>注册 `SecurityContextImpl` Bean</li>
 *   <li>注册 `SecurityContextFilter` 过滤器</li>
 *   <li>设置 `SecurityContextHolder` 的实现</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Configuration
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
    // 设置静态上下文实例
    AuthSecurityContextHolder.setContextInstance(securityContext);
    return new AuthSecurityContextHolder();
  }
}

