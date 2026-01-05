/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.exception;

/**
 * 权限异常
 *
 * <p>用于权限不足、Token 失效、角色不足等场景。 错误码范围：053000-053999
 *
 * <p>使用示例：
 *
 * <pre>
 * throw new PermissionException(CommonErrorCode.PERMISSION_DENIED, "权限不足");
 * </pre>
 *
 * @author Atlas Team
 * @date 2026-01-05
 */
public class PermissionException extends BusinessException {

  /**
   * 创建权限异常
   *
   * @param errorCode 错误码
   * @param message 错误消息
   */
  public PermissionException(String errorCode, String message) {
    super(errorCode, message);
  }

  /**
   * 创建权限异常（带原因）
   *
   * @param errorCode 错误码
   * @param message 错误消息
   * @param cause 异常原因
   */
  public PermissionException(String errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
