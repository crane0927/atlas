/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.logging.trace;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/** TraceIdInterceptor 单元测试 */
class TraceIdInterceptorTest {

  private TraceIdInterceptor interceptor;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private Object handler;

  @BeforeEach
  void setUp() {
    interceptor = new TraceIdInterceptor();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    handler = mock(Object.class);
    TraceIdUtil.clear();
    MDC.clear();
  }

  @AfterEach
  void tearDown() {
    TraceIdUtil.clear();
    MDC.clear();
  }

  @Test
  void testPreHandleWithTraceIdInHeader() {
    // Given
    String traceId = "test-trace-id-12345";
    when(request.getHeader("X-Trace-Id")).thenReturn(traceId);

    // When
    boolean result = interceptor.preHandle(request, response, handler);

    // Then
    assertTrue(result);
    assertEquals(traceId, TraceIdUtil.getTraceId());
    assertEquals(traceId, MDC.get("traceId"));
  }

  @Test
  void testPreHandleWithoutTraceIdInHeader() {
    // Given
    when(request.getHeader("X-Trace-Id")).thenReturn(null);

    // When
    boolean result = interceptor.preHandle(request, response, handler);

    // Then
    assertTrue(result);
    String generatedTraceId = TraceIdUtil.getTraceId();
    assertNotNull(generatedTraceId);
    assertEquals(32, generatedTraceId.length());
  }

  @Test
  void testPreHandleWithEmptyTraceIdInHeader() {
    // Given
    when(request.getHeader("X-Trace-Id")).thenReturn("");

    // When
    boolean result = interceptor.preHandle(request, response, handler);

    // Then
    assertTrue(result);
    String generatedTraceId = TraceIdUtil.getTraceId();
    assertNotNull(generatedTraceId);
    assertEquals(32, generatedTraceId.length());
  }

  @Test
  void testAfterCompletion() {
    // Given
    String traceId = "test-trace-id-12345";
    TraceIdUtil.setTraceId(traceId);
    Exception ex = null;

    // When
    interceptor.afterCompletion(request, response, handler, ex);

    // Then
    assertNull(TraceIdUtil.getTraceId());
    assertNull(MDC.get("traceId"));
  }

  @Test
  void testAfterCompletionWithException() {
    // Given
    String traceId = "test-trace-id-12345";
    TraceIdUtil.setTraceId(traceId);
    Exception ex = new RuntimeException("Test exception");

    // When
    interceptor.afterCompletion(request, response, handler, ex);

    // Then
    assertNull(TraceIdUtil.getTraceId());
    assertNull(MDC.get("traceId"));
  }
}
