/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.web.security;

import com.atlas.common.feature.security.context.SecurityContext;
import com.atlas.common.feature.security.context.SecurityContextHolder;
import org.springframework.beans.factory.InitializingBean;

/**
 * 将 Web 安全上下文注册到 SecurityContextHolder
 *
 * <p>在存在 SecurityContext Bean 时，将其注册到 {@link SecurityContextHolder#setContextProvider}，
 * 使 {@link SecurityContextHolder#getLoginUser()} 等静态方法生效，供审计、权限等使用。
 *
 * @author Atlas
 * @since 1.0.0
 */
public class SecurityContextHolderConfigurer implements InitializingBean {

  private final SecurityContext securityContext;

  public SecurityContextHolderConfigurer(SecurityContext securityContext) {
    this.securityContext = securityContext;
  }

  @Override
  public void afterPropertiesSet() {
    SecurityContextHolder.setContextProvider(() -> securityContext);
  }
}
