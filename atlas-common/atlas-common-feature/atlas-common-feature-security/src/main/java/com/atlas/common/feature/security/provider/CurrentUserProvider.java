/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.security.provider;

/**
 * 当前用户提供者
 *
 * <p>用于在审计等场景获取当前请求对应的用户名，不依赖静态 contextProvider，可由各应用通过 Filter/AOP 设置 ThreadLocal 后由此接口提供。参考
 * Oneself 的 CurrentUserProvider 设计。
 *
 * <p>实现类可从 {@link com.atlas.common.feature.security.context.SecurityContextHolder} 或自有 ThreadLocal
 * 等获取当前用户。
 *
 * @author Atlas
 * @since 1.0.0
 */
public interface CurrentUserProvider {

  /**
   * 获取当前用户名
   *
   * @return 当前登录用户名，未登录或无法获取时返回 null
   */
  String getCurrentUsername();
}
