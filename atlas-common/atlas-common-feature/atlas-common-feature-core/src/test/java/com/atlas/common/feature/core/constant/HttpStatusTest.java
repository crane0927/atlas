/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.constant;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

/** HttpStatus 类单元测试 */
class HttpStatusTest {

  @Test
  void testHttpStatusConstants() {
    // 验证 HTTP 状态码常量值
    assertEquals(200, HttpStatus.OK);
    assertEquals(201, HttpStatus.CREATED);
    assertEquals(204, HttpStatus.NO_CONTENT);
    assertEquals(400, HttpStatus.BAD_REQUEST);
    assertEquals(401, HttpStatus.UNAUTHORIZED);
    assertEquals(403, HttpStatus.FORBIDDEN);
    assertEquals(404, HttpStatus.NOT_FOUND);
    assertEquals(500, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void testHttpStatusCannotBeInstantiated() throws Exception {
    // Given
    Constructor<HttpStatus> constructor = HttpStatus.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    // When & Then
    InvocationTargetException exception =
        assertThrows(InvocationTargetException.class, constructor::newInstance);

    assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    assertEquals("常量类不允许实例化", exception.getCause().getMessage());
  }
}
