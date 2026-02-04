/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.security.provider;

import com.atlas.common.feature.security.context.SecurityContextHolder;
import com.atlas.common.feature.security.user.LoginUser;

/**
 * 基于 SecurityContextHolder 的当前用户提供者
 *
 * <p>从 {@link SecurityContextHolder#getLoginUser()} 获取当前用户并返回用户名，便于已注册 contextProvider 并设置
 * SecurityContext 的应用零改动使用。由各应用在配置类中注册为 Bean。
 *
 * @author Atlas
 * @since 1.0.0
 */
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {

  @Override
  public String getCurrentUsername() {
    LoginUser loginUser = SecurityContextHolder.getLoginUser();
    return loginUser != null ? loginUser.getUsername() : null;
  }
}
