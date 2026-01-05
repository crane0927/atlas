/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.exception;

import lombok.Getter;

/**
 * 基础业务异常类
 *
 * <p>所有业务异常的基类，包含错误码和错误消息。 支持异常链，便于问题追踪。
 *
 * <p>使用示例：
 *
 * <pre>
 * throw new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "用户不存在");
 * </pre>
 *
 * @author Atlas Team
 * @date 2026-01-05
 */
@Getter
public class BusinessException extends RuntimeException {

  /** 错误码（6位数字） */
  private final String errorCode;

  /**
   * 创建业务异常
   *
   * @param errorCode 错误码
   * @param message 错误消息
   */
  public BusinessException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * 创建业务异常（带原因）
   *
   * @param errorCode 错误码
   * @param message 错误消息
   * @param cause 异常原因
   */
  public BusinessException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }
}
