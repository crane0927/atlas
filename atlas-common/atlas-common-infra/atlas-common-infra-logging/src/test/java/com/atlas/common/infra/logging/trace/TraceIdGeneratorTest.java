/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.logging.trace;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** TraceIdGenerator 单元测试 */
class TraceIdGeneratorTest {

  @Test
  void testGenerateUUID() {
    // When
    String traceId = TraceIdGenerator.generateUUID();

    // Then
    assertNotNull(traceId);
    assertEquals(32, traceId.length());
    assertFalse(traceId.contains("-"));
    // 验证是十六进制字符
    assertTrue(traceId.matches("[0-9a-fA-F]{32}"));
  }

  @Test
  void testGenerateUUIDMultipleTimes() {
    // When
    String traceId1 = TraceIdGenerator.generateUUID();
    String traceId2 = TraceIdGenerator.generateUUID();

    // Then
    assertNotNull(traceId1);
    assertNotNull(traceId2);
    assertNotEquals(traceId1, traceId2);
  }

  @Test
  void testGenerateSnowflake() {
    // When
    String traceId = TraceIdGenerator.generateSnowflake();

    // Then
    assertNotNull(traceId);
    // 当前实现使用 UUID，所以长度是 32
    assertEquals(32, traceId.length());
  }
}
