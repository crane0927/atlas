/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.logging.trace;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/** TraceIdUtil 单元测试 */
class TraceIdUtilTest {

  @BeforeEach
  void setUp() {
    TraceIdUtil.clear();
    MDC.clear();
  }

  @AfterEach
  void tearDown() {
    TraceIdUtil.clear();
    MDC.clear();
  }

  @Test
  void testSetTraceId() {
    // Given
    String traceId = "test-trace-id-12345";

    // When
    TraceIdUtil.setTraceId(traceId);

    // Then
    assertEquals(traceId, TraceIdUtil.getTraceId());
    assertEquals(traceId, MDC.get("traceId"));
  }

  @Test
  void testSetTraceIdWithNull() {
    // When & Then
    assertThrows(IllegalArgumentException.class, () -> TraceIdUtil.setTraceId(null));
  }

  @Test
  void testGetTraceIdFromThreadLocal() {
    // Given
    String traceId = "test-trace-id-12345";
    TraceIdUtil.setTraceId(traceId);
    // 清除 MDC，验证优先从 ThreadLocal 获取
    MDC.remove("traceId");

    // When
    String result = TraceIdUtil.getTraceId();

    // Then
    assertEquals(traceId, result);
  }

  @Test
  void testGetTraceIdFromMDC() {
    // Given
    String traceId = "test-trace-id-12345";
    MDC.put("traceId", traceId);
    // 不设置 ThreadLocal

    // When
    String result = TraceIdUtil.getTraceId();

    // Then
    assertEquals(traceId, result);
  }

  @Test
  void testGetTraceIdWhenEmpty() {
    // When
    String result = TraceIdUtil.getTraceId();

    // Then
    assertNull(result);
  }

  @Test
  void testClear() {
    // Given
    String traceId = "test-trace-id-12345";
    TraceIdUtil.setTraceId(traceId);

    // When
    TraceIdUtil.clear();

    // Then
    assertNull(TraceIdUtil.getTraceId());
    assertNull(MDC.get("traceId"));
  }

  @Test
  void testGenerate() {
    // When
    String traceId = TraceIdUtil.generate();

    // Then
    assertNotNull(traceId);
    assertEquals(32, traceId.length());
    assertFalse(traceId.contains("-"));
  }

  @Test
  void testThreadIsolation() throws InterruptedException {
    // Given
    String traceId1 = "trace-id-1";
    String traceId2 = "trace-id-2";

    // When
    TraceIdUtil.setTraceId(traceId1);

    Thread thread =
        new Thread(
            () -> {
              // 新线程中没有 TraceId
              assertNull(TraceIdUtil.getTraceId());
              // 设置新线程的 TraceId
              TraceIdUtil.setTraceId(traceId2);
              assertEquals(traceId2, TraceIdUtil.getTraceId());
            });

    thread.start();
    thread.join();

    // Then - 主线程的 TraceId 不受影响
    assertEquals(traceId1, TraceIdUtil.getTraceId());
  }
}
