/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.context;

import com.atlas.common.feature.security.context.SecurityContext;
import com.atlas.common.feature.security.context.SecurityContextHolder;

/**
 * Auth 模块的 SecurityContextHolder 实现
 *
 * <p>扩展 `SecurityContextHolder` 抽象类，提供具体的安全上下文实现。
 *
 * <p>功能特性：
 * <ul>
 *   <li>提供静态方法 `getContext()` 的实现</li>
 *   <li>使用 `SecurityContextImpl` 作为底层实现</li>
 *   <li>支持通过 Spring 注入 `SecurityContextImpl`</li>
 * </ul>
 *
 * <p>注意：由于 `SecurityContextHolder.getContext()` 是静态方法，无法通过 Spring 注入。
 * 这里使用单例模式，通过静态变量持有 `SecurityContextImpl` 实例。
 *
 * <p>使用方式：
 * <pre>{@code
 * // 在 SecurityConfig 中初始化
 * AuthSecurityContextHolder.setContextInstance(securityContext);
 *
 * // 在下游服务中使用（注意：需要导入 AuthSecurityContextHolder 而不是 SecurityContextHolder）
 * LoginUser user = AuthSecurityContextHolder.getLoginUser();
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class AuthSecurityContextHolder extends SecurityContextHolder {

  private static SecurityContextImpl contextInstance;

  /**
   * 设置 SecurityContext 实例
   *
   * <p>由 Spring 容器调用，设置 `SecurityContextImpl` 实例。
   *
   * @param context SecurityContextImpl 实例
   */
  public static void setContextInstance(SecurityContextImpl context) {
    contextInstance = context;
  }

  /**
   * 获取当前安全上下文
   *
   * <p>返回 `SecurityContextImpl` 实例，供静态方法使用。
   *
   * <p>注意：此方法隐藏了父类的静态方法，提供具体实现。
   * 下游服务应该使用 `AuthSecurityContextHolder.getLoginUser()` 而不是 `SecurityContextHolder.getLoginUser()`。
   *
   * @return 当前安全上下文，如果未初始化则返回 null
   */
  public static SecurityContext getContext() {
    return contextInstance;
  }
}

