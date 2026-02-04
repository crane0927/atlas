/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.security.context;

import com.atlas.common.feature.security.user.LoginUser;
import java.util.function.Supplier;

/**
 * 安全上下文持有者
 *
 * <p>提供静态方法获取安全上下文，方便开发人员使用。支持可插拔实现：通过 {@link
 * #setContextProvider(Supplier)} 注册具体实现后，{@link
 * #getContext()} 将委托给该提供者；未注册时返回 null，不抛异常。
 *
 * <p>使用示例：
 *
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
 * <p>实现方式：
 *
 * <ul>
 *   <li>在应用启动时（如 Auth 模块）调用 {@link #setContextProvider(Supplier)} 注册提供者
 *   <li>未注册时 {@link #getContext()} 返回 null，{@link #getLoginUser()} 返回 null，便于审计等逻辑使用默认值
 * </ul>
 *
 * @author Atlas
 * @since 1.0.0
 */
public abstract class SecurityContextHolder {

  private static volatile Supplier<SecurityContext> contextProvider;

  /**
   * 注册安全上下文提供者
   *
   * <p>由具体实现模块（如 atlas-auth）在启动时调用，注册后 {@link #getContext()} 将委托给该提供者。
   *
   * @param provider 提供者，传入 null 表示清除注册
   */
  public static void setContextProvider(Supplier<SecurityContext> provider) {
    contextProvider = provider;
  }

  /**
   * 获取当前安全上下文
   *
   * <p>若已通过 {@link #setContextProvider(Supplier)} 注册提供者，则委托给该提供者；否则返回 null。
   *
   * @return 当前安全上下文，未注册或不存在时返回 null
   */
  public static SecurityContext getContext() {
    if (contextProvider != null) {
      return contextProvider.get();
    }
    return null;
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
