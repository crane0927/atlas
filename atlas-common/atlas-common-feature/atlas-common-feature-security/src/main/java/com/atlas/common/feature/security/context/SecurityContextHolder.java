/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.context;

import com.atlas.common.feature.security.user.LoginUser;

/**
 * 安全上下文持有者
 *
 * <p>提供静态方法获取安全上下文，方便开发人员使用。该抽象类不包含具体实现，
 * getContext() 方法由具体实现提供。
 *
 * <p>使用示例：
 * <pre>
 * // 获取当前登录用户
 * LoginUser user = SecurityContextHolder.getLoginUser();
 *
 * // 判断是否已认证
 * if (SecurityContextHolder.isAuthenticated()) {
 *     // 已认证逻辑
 * }
 * </pre>
 *
 * <p>实现类应该：
 * <ul>
 *   <li>实现 getContext() 方法，提供具体的安全上下文实现</li>
 *   <li>支持 ThreadLocal、Request Scope、全局单例等多种实现方式</li>
 * </ul>
 *
 * @author Atlas
 * @since 1.0.0
 */
public abstract class SecurityContextHolder {

  /**
   * 获取当前安全上下文
   *
   * <p>该方法由具体实现提供，返回当前线程或请求的安全上下文。
   *
   * @return 当前安全上下文，如果不存在返回 null
   */
  public static SecurityContext getContext() {
    throw new UnsupportedOperationException("需要具体实现");
  }

  /**
   * 获取当前登录用户
   *
   * <p>便捷方法，基于 getContext() 实现。
   *
   * @return 当前登录用户，未登录时返回 null
   */
  public static LoginUser getLoginUser() {
    SecurityContext context = getContext();
    return context != null ? context.getLoginUser() : null;
  }

  /**
   * 判断当前是否已认证
   *
   * <p>便捷方法，基于 getContext() 实现。
   *
   * @return 如果已认证返回 true，否则返回 false
   */
  public static boolean isAuthenticated() {
    SecurityContext context = getContext();
    return context != null && context.isAuthenticated();
  }
}

