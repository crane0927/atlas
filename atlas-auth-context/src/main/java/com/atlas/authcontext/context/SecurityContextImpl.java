/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.authcontext.context;

import com.atlas.common.feature.security.context.SecurityContext;
import com.atlas.common.feature.security.user.LoginUser;

/**
 * 安全上下文实现（方案 B 抽取至 auth-context）
 *
 * <p>使用 ThreadLocal 存储用户信息，供 Filter 设置、SecurityContextHolder 读取。
 *
 * @author Atlas
 * @since 1.0.0
 */
public class SecurityContextImpl implements SecurityContext {

  private static final ThreadLocal<LoginUser> context = new ThreadLocal<>();

  public void setLoginUser(LoginUser loginUser) {
    if (loginUser != null) {
      context.set(loginUser);
    } else {
      context.remove();
    }
  }

  @Override
  public LoginUser getLoginUser() {
    return context.get();
  }

  @Override
  public boolean isAuthenticated() {
    return context.get() != null;
  }

  @Override
  public void clear() {
    context.remove();
  }
}
