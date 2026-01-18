/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.common.infra.logging.trace.TraceIdUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TraceIdFilter 单元测试
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class TraceIdFilterTest {

  private TraceIdFilter filter;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    filter = new TraceIdFilter();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    filterChain = mock(FilterChain.class);
    TraceIdUtil.clear();
  }

  @AfterEach
  void tearDown() {
    TraceIdUtil.clear();
  }

  @Test
  void testDoFilterWithTraceIdInHeader() throws ServletException, IOException {
    // Given
    String traceId = "test-trace-id-12345";
    when(request.getHeader("X-Trace-Id")).thenReturn(traceId);
    final String[] actualTraceId = new String[1];
    doAnswer(
            invocation -> {
              // 在 filter 执行过程中检查 TraceId
              actualTraceId[0] = TraceIdUtil.getTraceId();
              return null;
            })
        .when(filterChain)
        .doFilter(request, response);

    // When
    filter.doFilter(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    assertEquals(traceId, actualTraceId[0]);
    // 验证响应头未添加（默认不添加）
    verify(response, never()).setHeader("X-Trace-Id", traceId);
    // 验证 TraceId 已被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testDoFilterWithoutTraceIdInHeader() throws ServletException, IOException {
    // Given
    when(request.getHeader("X-Trace-Id")).thenReturn(null);
    final String[] actualTraceId = new String[1];
    doAnswer(
            invocation -> {
              // 在 filter 执行过程中检查 TraceId
              actualTraceId[0] = TraceIdUtil.getTraceId();
              return null;
            })
        .when(filterChain)
        .doFilter(request, response);

    // When
    filter.doFilter(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    assertNotNull(actualTraceId[0]);
    // 验证响应头未添加（默认不添加）
    verify(response, never()).setHeader(any(), any());
    // 验证 TraceId 已被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testDoFilterWithEmptyTraceIdInHeader() throws ServletException, IOException {
    // Given
    when(request.getHeader("X-Trace-Id")).thenReturn("");
    final String[] actualTraceId = new String[1];
    doAnswer(
            invocation -> {
              // 在 filter 执行过程中检查 TraceId
              actualTraceId[0] = TraceIdUtil.getTraceId();
              return null;
            })
        .when(filterChain)
        .doFilter(request, response);

    // When
    filter.doFilter(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    assertNotNull(actualTraceId[0]);
    // 验证响应头未添加（默认不添加）
    verify(response, never()).setHeader(any(), any());
    // 验证 TraceId 已被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testDoFilterWithAddResponseHeaderEnabled() throws ServletException, IOException {
    // Given
    String traceId = "test-trace-id-12345";
    when(request.getHeader("X-Trace-Id")).thenReturn(traceId);
    filter.setAddResponseHeader(true);
    final String[] actualTraceId = new String[1];
    doAnswer(
            invocation -> {
              // 在 filter 执行过程中检查 TraceId
              actualTraceId[0] = TraceIdUtil.getTraceId();
              return null;
            })
        .when(filterChain)
        .doFilter(request, response);

    // When
    filter.doFilter(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    assertEquals(traceId, actualTraceId[0]);
    // 验证响应头已添加
    verify(response).setHeader("X-Trace-Id", traceId);
    // 验证 TraceId 已被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testDoFilterClearsTraceIdAfterRequest() throws ServletException, IOException {
    // Given
    String traceId = "test-trace-id-12345";
    when(request.getHeader("X-Trace-Id")).thenReturn(traceId);

    // When
    filter.doFilter(request, response, filterChain);

    // Then
    // TraceId 应该在 finally 块中被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testDoFilterClearsTraceIdEvenOnException() throws ServletException, IOException {
    // Given
    String traceId = "test-trace-id-12345";
    when(request.getHeader("X-Trace-Id")).thenReturn(traceId);
    IOException exception = new IOException("Test exception");
    doThrow(exception).when(filterChain).doFilter(request, response);

    // When & Then
    try {
      filter.doFilter(request, response, filterChain);
    } catch (IOException e) {
      // 预期会抛出异常
    }

    // TraceId 应该在 finally 块中被清理，即使发生异常
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testDoFilterGeneratesNewTraceIdWhenHeaderIsMissing() throws ServletException, IOException {
    // Given
    when(request.getHeader("X-Trace-Id")).thenReturn(null);
    final String[] actualTraceId = new String[1];
    doAnswer(
            invocation -> {
              // 在 filter 执行过程中检查 TraceId
              actualTraceId[0] = TraceIdUtil.getTraceId();
              return null;
            })
        .when(filterChain)
        .doFilter(request, response);

    // When
    filter.doFilter(request, response, filterChain);

    // Then
    assertNotNull(actualTraceId[0]);
    // 验证生成的 TraceId 格式正确（32 位 UUID，去除连字符）
    assertEquals(32, actualTraceId[0].length());
    // 验证 TraceId 已被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testSetAddResponseHeader() {
    // When
    filter.setAddResponseHeader(true);

    // Then
    // 验证设置成功（通过后续测试验证）
  }
}
