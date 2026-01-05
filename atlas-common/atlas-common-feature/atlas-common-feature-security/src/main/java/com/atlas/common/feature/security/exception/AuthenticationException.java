/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.exception;

/**
 * 认证异常接口
 *
 * <p>表示用户未认证或认证失败。该接口仅定义抽象方法，不包含具体实现。
 * 具体实现可以定义自己的异常类来实现此接口。
 *
 * <p>使用场景：
 * <ul>
 *   <li>用户未登录时访问需要认证的资源</li>
 *   <li>Token 无效或过期</li>
 *   <li>认证信息缺失</li>
 * </ul>
 *
 * @author Atlas
 * @since 1.0.0
 */
public interface AuthenticationException {

  /**
   * 获取异常消息
   *
   * @return 异常消息
   */
  String getMessage();
}

