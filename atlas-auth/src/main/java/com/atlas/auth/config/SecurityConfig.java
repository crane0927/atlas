/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.auth.config;

import com.atlas.auth.context.AuthSecurityContextHolder;
import com.atlas.common.feature.security.context.SecurityContext;
import com.atlas.common.feature.security.context.SecurityContextHolder;
import com.atlas.common.feature.security.provider.CurrentUserProvider;
import com.atlas.common.feature.security.provider.SecurityContextCurrentUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 安全配置类（方案 B：Filter 与 SecurityContextImpl 由 atlas-auth-context 提供）
 *
 * <p>功能：将 auth-context 的 SecurityContext 注册到 SecurityContextHolder，并注册 CurrentUserProvider。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Configuration("authSecurityConfig")
public class SecurityConfig {

  /**
   * 将 auth-context 的 SecurityContext 注册到静态 Holder，供 getLoginUser() 等使用。
   *
   * @param securityContext 由 atlas-auth-context 提供的 SecurityContextImpl
   * @return AuthSecurityContextHolder 实例
   */
  @Bean
  public AuthSecurityContextHolder authSecurityContextHolder(SecurityContext securityContext) {
    AuthSecurityContextHolder.setContextInstance(securityContext);
    SecurityContextHolder.setContextProvider(AuthSecurityContextHolder::getContext);
    return new AuthSecurityContextHolder();
  }

  @Bean
  public CurrentUserProvider currentUserProvider() {
    return new SecurityContextCurrentUserProvider();
  }
}
