/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.exception;

/**
 * 授权异常接口
 *
 * <p>表示用户权限不足。该接口仅定义抽象方法，不包含具体实现。
 * 具体实现可以定义自己的异常类来实现此接口。
 *
 * <p>使用场景：
 * <ul>
 *   <li>用户权限不足时访问受保护资源</li>
 *   <li>用户角色不符合要求</li>
 *   <li>权限检查失败</li>
 * </ul>
 *
 * @author Atlas
 * @since 1.0.0
 */
public interface AuthorizationException {

  /**
   * 获取异常消息
   *
   * @return 异常消息
   */
  String getMessage();
}

