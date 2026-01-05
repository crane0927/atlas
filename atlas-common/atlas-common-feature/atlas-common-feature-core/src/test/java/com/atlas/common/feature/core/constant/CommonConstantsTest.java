/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.constant;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

/** CommonConstants 类单元测试 */
class CommonConstantsTest {

  @Test
  void testCommonConstantsValues() {
    // 验证通用常量值
    assertEquals("", CommonConstants.EMPTY_STRING);
    assertEquals(1, CommonConstants.DEFAULT_PAGE);
    assertEquals(10, CommonConstants.DEFAULT_SIZE);
    assertEquals(1000, CommonConstants.MAX_PAGE_SIZE);
    assertEquals("000000", CommonConstants.SUCCESS_CODE);
    assertEquals("操作成功", CommonConstants.SUCCESS_MESSAGE);
  }

  @Test
  void testCommonConstantsCannotBeInstantiated() throws Exception {
    // Given
    Constructor<CommonConstants> constructor = CommonConstants.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    // When & Then
    InvocationTargetException exception =
        assertThrows(InvocationTargetException.class, constructor::newInstance);

    assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    assertEquals("常量类不允许实例化", exception.getCause().getMessage());
  }
}
