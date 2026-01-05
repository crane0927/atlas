/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.exception;

/**
 * 数据异常
 *
 * <p>用于数据冲突、数据格式错误、数据完整性错误等场景。 错误码范围：054000-054999
 *
 * <p>使用示例：
 *
 * <pre>
 * throw new DataException(CommonErrorCode.DATA_CONFLICT, "用户名已存在");
 * </pre>
 *
 * @author Atlas Team
 * @date 2026-01-05
 */
public class DataException extends BusinessException {

  /**
   * 创建数据异常
   *
   * @param errorCode 错误码
   * @param message 错误消息
   */
  public DataException(String errorCode, String message) {
    super(errorCode, message);
  }

  /**
   * 创建数据异常（带原因）
   *
   * @param errorCode 错误码
   * @param message 错误消息
   * @param cause 异常原因
   */
  public DataException(String errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
