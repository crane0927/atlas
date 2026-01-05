/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.exception;

/**
 * 参数异常
 *
 * <p>用于参数校验失败、必填项缺失、格式错误等场景。 错误码范围：051000-051999
 *
 * <p>使用示例：
 *
 * <pre>
 * throw new ParameterException(CommonErrorCode.PARAM_REQUIRED, "用户名不能为空");
 * </pre>
 *
 * @author Atlas Team
 * @date 2026-01-05
 */
public class ParameterException extends BusinessException {

  /**
   * 创建参数异常
   *
   * @param errorCode 错误码
   * @param message 错误消息
   */
  public ParameterException(String errorCode, String message) {
    super(errorCode, message);
  }

  /**
   * 创建参数异常（带原因）
   *
   * @param errorCode 错误码
   * @param message 错误消息
   * @param cause 异常原因
   */
  public ParameterException(String errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
