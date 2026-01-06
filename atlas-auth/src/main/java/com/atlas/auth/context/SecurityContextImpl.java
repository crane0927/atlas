/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.context;

import com.atlas.common.feature.security.context.SecurityContext;
import com.atlas.common.feature.security.user.LoginUser;

/**
 * 安全上下文实现类
 *
 * <p>使用 ThreadLocal 存储用户信息，为下游服务提供安全上下文。
 *
 * <p>实现特性：
 * <ul>
 *   <li>使用 ThreadLocal 存储用户信息，线程安全</li>
 *   <li>支持多线程环境下的用户上下文隔离</li>
 *   <li>提供 clear() 方法清理上下文，避免内存泄漏</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class SecurityContextImpl implements SecurityContext {

  private static final ThreadLocal<LoginUser> context = new ThreadLocal<>();

  /**
   * 设置当前登录用户
   *
   * <p>将用户信息存储到 ThreadLocal 中。
   *
   * @param loginUser 登录用户信息
   */
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

