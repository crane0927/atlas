/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.constant;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

/** CommonErrorCode 类单元测试 */
class CommonErrorCodeTest {

  @Test
  void testErrorCodeFormat() {
    // 验证所有错误码都是 6 位数字
    assertTrue(CommonErrorCode.SYSTEM_ERROR.matches("\\d{6}"));
    assertTrue(CommonErrorCode.SERVICE_UNAVAILABLE.matches("\\d{6}"));
    assertTrue(CommonErrorCode.REQUEST_TIMEOUT.matches("\\d{6}"));
    assertTrue(CommonErrorCode.PARAM_ERROR.matches("\\d{6}"));
    assertTrue(CommonErrorCode.PARAM_REQUIRED.matches("\\d{6}"));
    assertTrue(CommonErrorCode.PARAM_FORMAT_ERROR.matches("\\d{6}"));
    assertTrue(CommonErrorCode.BUSINESS_ERROR.matches("\\d{6}"));
    assertTrue(CommonErrorCode.DATA_NOT_FOUND.matches("\\d{6}"));
    assertTrue(CommonErrorCode.STATUS_INVALID.matches("\\d{6}"));
    assertTrue(CommonErrorCode.PERMISSION_DENIED.matches("\\d{6}"));
    assertTrue(CommonErrorCode.TOKEN_INVALID.matches("\\d{6}"));
    assertTrue(CommonErrorCode.DATA_ERROR.matches("\\d{6}"));
    assertTrue(CommonErrorCode.DATA_CONFLICT.matches("\\d{6}"));
  }

  @Test
  void testErrorCodeValues() {
    // 验证系统错误码 (050000-050999)
    assertEquals("050000", CommonErrorCode.SYSTEM_ERROR);
    assertEquals("050001", CommonErrorCode.SERVICE_UNAVAILABLE);
    assertEquals("050002", CommonErrorCode.REQUEST_TIMEOUT);

    // 验证参数错误码 (051000-051999)
    assertEquals("051000", CommonErrorCode.PARAM_ERROR);
    assertEquals("051001", CommonErrorCode.PARAM_REQUIRED);
    assertEquals("051002", CommonErrorCode.PARAM_FORMAT_ERROR);

    // 验证业务错误码 (052000-052999)
    assertEquals("052000", CommonErrorCode.BUSINESS_ERROR);
    assertEquals("052001", CommonErrorCode.DATA_NOT_FOUND);
    assertEquals("052002", CommonErrorCode.STATUS_INVALID);

    // 验证权限错误码 (053000-053999)
    assertEquals("053000", CommonErrorCode.PERMISSION_DENIED);
    assertEquals("053001", CommonErrorCode.TOKEN_INVALID);

    // 验证数据错误码 (054000-054999)
    assertEquals("054000", CommonErrorCode.DATA_ERROR);
    assertEquals("054001", CommonErrorCode.DATA_CONFLICT);
  }

  @Test
  void testErrorCodeModuleCode() {
    // 验证所有错误码的模块码都是 05
    assertTrue(CommonErrorCode.SYSTEM_ERROR.startsWith("05"));
    assertTrue(CommonErrorCode.PARAM_ERROR.startsWith("05"));
    assertTrue(CommonErrorCode.BUSINESS_ERROR.startsWith("05"));
    assertTrue(CommonErrorCode.PERMISSION_DENIED.startsWith("05"));
    assertTrue(CommonErrorCode.DATA_ERROR.startsWith("05"));
  }

  @Test
  void testErrorCodeCannotBeInstantiated() throws Exception {
    // Given
    Constructor<CommonErrorCode> constructor = CommonErrorCode.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    // When & Then
    InvocationTargetException exception =
        assertThrows(InvocationTargetException.class, constructor::newInstance);

    assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    assertEquals("常量类不允许实例化", exception.getCause().getMessage());
  }
}
