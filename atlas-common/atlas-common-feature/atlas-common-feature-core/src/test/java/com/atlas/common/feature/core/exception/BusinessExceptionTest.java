/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.atlas.common.feature.core.constant.CommonErrorCode;
import org.junit.jupiter.api.Test;

/** BusinessException 类单元测试 */
class BusinessExceptionTest {

  @Test
  void testCreateBusinessException() {
    // Given
    String errorCode = CommonErrorCode.DATA_NOT_FOUND;
    String message = "数据不存在";

    // When
    BusinessException exception = new BusinessException(errorCode, message);

    // Then
    assertNotNull(exception);
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testCreateBusinessExceptionWithCause() {
    // Given
    String errorCode = CommonErrorCode.SYSTEM_ERROR;
    String message = "系统错误";
    Throwable cause = new RuntimeException("原始异常");

    // When
    BusinessException exception = new BusinessException(errorCode, message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testThrowBusinessException() {
    // Given
    String errorCode = CommonErrorCode.DATA_NOT_FOUND;
    String message = "数据不存在";

    // When & Then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              throw new BusinessException(errorCode, message);
            });

    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(message, exception.getMessage());
  }

  @Test
  void testParameterException() {
    // Given
    String errorCode = CommonErrorCode.PARAM_REQUIRED;
    String message = "参数不能为空";

    // When
    ParameterException exception = new ParameterException(errorCode, message);

    // Then
    assertNotNull(exception);
    assertTrue(exception instanceof BusinessException);
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(message, exception.getMessage());
  }

  @Test
  void testPermissionException() {
    // Given
    String errorCode = CommonErrorCode.PERMISSION_DENIED;
    String message = "权限不足";

    // When
    PermissionException exception = new PermissionException(errorCode, message);

    // Then
    assertNotNull(exception);
    assertTrue(exception instanceof BusinessException);
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(message, exception.getMessage());
  }

  @Test
  void testDataException() {
    // Given
    String errorCode = CommonErrorCode.DATA_CONFLICT;
    String message = "数据冲突";

    // When
    DataException exception = new DataException(errorCode, message);

    // Then
    assertNotNull(exception);
    assertTrue(exception instanceof BusinessException);
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(message, exception.getMessage());
  }

  @Test
  void testExceptionChain() {
    // Given
    String errorCode = CommonErrorCode.SYSTEM_ERROR;
    String message = "系统错误";
    RuntimeException cause = new RuntimeException("原始异常");

    // When
    BusinessException exception = new BusinessException(errorCode, message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(cause, exception.getCause());
    assertEquals("原始异常", exception.getCause().getMessage());
  }

  @Test
  void testParameterExceptionWithCause() {
    // Given
    String errorCode = CommonErrorCode.PARAM_FORMAT_ERROR;
    String message = "参数格式错误";
    IllegalArgumentException cause = new IllegalArgumentException("格式不正确");

    // When
    ParameterException exception = new ParameterException(errorCode, message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testPermissionExceptionWithCause() {
    // Given
    String errorCode = CommonErrorCode.TOKEN_INVALID;
    String message = "Token 无效";
    SecurityException cause = new SecurityException("Token 已过期");

    // When
    PermissionException exception = new PermissionException(errorCode, message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testDataExceptionWithCause() {
    // Given
    String errorCode = CommonErrorCode.DATA_ERROR;
    String message = "数据错误";
    IllegalStateException cause = new IllegalStateException("数据状态不正确");

    // When
    DataException exception = new DataException(errorCode, message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(errorCode, exception.getErrorCode());
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }
}
