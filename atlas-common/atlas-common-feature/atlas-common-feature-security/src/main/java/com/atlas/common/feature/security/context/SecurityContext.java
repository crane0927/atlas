/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.context;

import com.atlas.common.feature.security.user.LoginUser;

/**
 * 安全上下文接口
 *
 * <p>提供获取当前登录用户信息和认证状态的方法。该接口只负责提供安全上下文信息，
 * 不负责设置，设置逻辑由具体实现负责（ThreadLocal、Request Scope 等）。
 *
 * <p>实现类应该确保：
 * <ul>
 *   <li>getLoginUser() 未登录时应返回 null</li>
 *   <li>isAuthenticated() 应该与 getLoginUser() != null 保持一致</li>
 *   <li>clear() 方法由实现类决定是否提供</li>
 * </ul>
 *
 * <p>实现方式：
 * <ul>
 *   <li>ThreadLocal: 线程本地存储，适用于单线程请求处理</li>
 *   <li>Request Scope: 请求作用域，适用于 Web 应用</li>
 *   <li>全局单例: 适用于单用户应用（不推荐）</li>
 * </ul>
 *
 * @author Atlas
 * @since 1.0.0
 */
public interface SecurityContext {

  /**
   * 获取当前登录用户
   *
   * <p>如果当前用户未登录，应返回 null。
   *
   * @return 当前登录用户，未登录时返回 null
   */
  LoginUser getLoginUser();

  /**
   * 判断当前是否已认证
   *
   * <p>该方法应该与 getLoginUser() != null 保持一致。
   *
   * @return 如果已认证返回 true，否则返回 false
   */
  boolean isAuthenticated();

  /**
   * 清除当前安全上下文
   *
   * <p>该方法由实现类决定是否提供。调用后，getLoginUser() 应该返回 null。
   */
  void clear();
}

