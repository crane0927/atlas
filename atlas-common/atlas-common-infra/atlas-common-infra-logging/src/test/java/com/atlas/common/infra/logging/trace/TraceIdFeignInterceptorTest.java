/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.logging.trace;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/** TraceIdFeignInterceptor 单元测试 */
class TraceIdFeignInterceptorTest {

  private TraceIdFeignInterceptor interceptor;
  private RequestTemplate template;

  @BeforeEach
  void setUp() {
    interceptor = new TraceIdFeignInterceptor();
    template = mock(RequestTemplate.class);
    TraceIdUtil.clear();
    MDC.clear();
  }

  @AfterEach
  void tearDown() {
    TraceIdUtil.clear();
    MDC.clear();
  }

  @Test
  void testApplyWithTraceId() {
    // Given
    String traceId = "test-trace-id-12345";
    TraceIdUtil.setTraceId(traceId);

    // When
    interceptor.apply(template);

    // Then
    verify(template).header("X-Trace-Id", traceId);
  }

  @Test
  void testApplyWithoutTraceId() {
    // Given - TraceId 未设置

    // When
    interceptor.apply(template);

    // Then
    verify(template, never()).header(anyString(), anyString());
  }

  @Test
  void testApplyWithEmptyTraceId() {
    // Given
    TraceIdUtil.setTraceId("");

    // When
    interceptor.apply(template);

    // Then
    verify(template, never()).header(anyString(), anyString());
  }
}
