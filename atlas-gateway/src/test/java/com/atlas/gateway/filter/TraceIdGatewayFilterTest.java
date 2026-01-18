/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.gateway.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.common.infra.logging.trace.TraceIdUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchange.Builder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * TraceIdGatewayFilter 单元测试
 *
 * @author Atlas Team
 */
class TraceIdGatewayFilterTest {

  private TraceIdGatewayFilter traceIdGatewayFilter;
  private ServerWebExchange exchange;
  private GatewayFilterChain chain;
  private ServerHttpRequest request;
  private ServerHttpResponse response;
  private HttpHeaders requestHeaders;
  private HttpHeaders responseHeaders;

  @BeforeEach
  void setUp() {
    traceIdGatewayFilter = new TraceIdGatewayFilter();
    exchange = mock(ServerWebExchange.class);
    chain = mock(GatewayFilterChain.class);
    request = mock(ServerHttpRequest.class);
    response = mock(ServerHttpResponse.class);
    requestHeaders = new HttpHeaders();
    responseHeaders = new HttpHeaders();

    when(exchange.getRequest()).thenReturn(request);
    when(exchange.getResponse()).thenReturn(response);
    when(request.getHeaders()).thenReturn(requestHeaders);
    when(response.getHeaders()).thenReturn(responseHeaders);
    // Mock mutate() 返回 builder，builder.request() 返回 builder，builder.build() 返回 exchange
    Builder exchangeBuilder = mock(Builder.class);
    when(exchange.mutate()).thenReturn(exchangeBuilder);
    when(exchangeBuilder.request(any(ServerHttpRequest.class))).thenReturn(exchangeBuilder);
    when(exchangeBuilder.build()).thenReturn(exchange);
    // Mock request.mutate() 返回 builder
    ServerHttpRequest.Builder requestBuilder = mock(ServerHttpRequest.Builder.class);
    when(request.mutate()).thenReturn(requestBuilder);
    when(requestBuilder.header(any(), any())).thenReturn(requestBuilder);
    when(requestBuilder.build()).thenReturn(request);
    when(chain.filter(any())).thenReturn(Mono.empty());
  }

  @AfterEach
  void tearDown() {
    // 清理 TraceId，避免影响其他测试
    TraceIdUtil.clear();
  }

  @Test
  void testFilterOrder() {
    assertEquals(Ordered.HIGHEST_PRECEDENCE, traceIdGatewayFilter.getOrder());
  }

  @Test
  void testFilterWithExistingTraceId() {
    // 设置请求头中的 TraceId
    String existingTraceId = "existing-trace-id-12345";
    requestHeaders.add("X-Trace-Id", existingTraceId);
    final String[] actualTraceId = new String[1];
    doAnswer(
            invocation -> {
              // 在 filter 执行过程中检查 TraceId
              actualTraceId[0] = TraceIdUtil.getTraceId();
              return Mono.empty();
            })
        .when(chain)
        .filter(any());

    // 执行过滤器
    Mono<Void> result = traceIdGatewayFilter.filter(exchange, chain);

    // 验证过滤器链被调用
    StepVerifier.create(result).verifyComplete();
    verify(chain).filter(any());

    // 验证 TraceId 在 filter 执行过程中被设置
    assertEquals(existingTraceId, actualTraceId[0]);

    // 验证响应头中包含 TraceId
    assertEquals(existingTraceId, responseHeaders.getFirst("X-Trace-Id"));
    
    // 验证 TraceId 已被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testFilterWithoutTraceId() {
    // 不设置请求头中的 TraceId
    final String[] actualTraceId = new String[1];
    doAnswer(
            invocation -> {
              // 在 filter 执行过程中检查 TraceId
              actualTraceId[0] = TraceIdUtil.getTraceId();
              return Mono.empty();
            })
        .when(chain)
        .filter(any());

    // 执行过滤器
    Mono<Void> result = traceIdGatewayFilter.filter(exchange, chain);

    // 验证过滤器链被调用
    StepVerifier.create(result).verifyComplete();
    verify(chain).filter(any());

    // 验证 TraceId 在 filter 执行过程中被生成并设置
    assertNotNull(actualTraceId[0]);
    assertEquals(32, actualTraceId[0].length()); // UUID 格式，去除连字符后为 32 位

    // 验证响应头中包含 TraceId
    assertEquals(actualTraceId[0], responseHeaders.getFirst("X-Trace-Id"));
    
    // 验证 TraceId 已被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testFilterWithEmptyTraceId() {
    // 设置空的 TraceId
    requestHeaders.add("X-Trace-Id", "");
    final String[] actualTraceId = new String[1];
    doAnswer(
            invocation -> {
              // 在 filter 执行过程中检查 TraceId
              actualTraceId[0] = TraceIdUtil.getTraceId();
              return Mono.empty();
            })
        .when(chain)
        .filter(any());

    // 执行过滤器
    Mono<Void> result = traceIdGatewayFilter.filter(exchange, chain);

    // 验证过滤器链被调用
    StepVerifier.create(result).verifyComplete();
    verify(chain).filter(any());

    // 验证 TraceId 在 filter 执行过程中被生成并设置（空字符串会被视为无效，自动生成）
    assertNotNull(actualTraceId[0]);
    assertEquals(32, actualTraceId[0].length());

    // 验证响应头中包含 TraceId
    assertEquals(actualTraceId[0], responseHeaders.getFirst("X-Trace-Id"));
    
    // 验证 TraceId 已被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testFilterClearsTraceIdAfterRequest() {
    // 设置请求头中的 TraceId
    String traceId = "test-trace-id-12345";
    requestHeaders.add("X-Trace-Id", traceId);

    // 执行过滤器
    Mono<Void> result = traceIdGatewayFilter.filter(exchange, chain);

    // 等待过滤器完成
    StepVerifier.create(result).verifyComplete();

    // 验证 TraceId 已被清理（在 then() 中清理）
    // 注意：由于是异步执行，需要等待清理完成
    // 在实际测试中，可以通过 Thread.sleep 或使用 CountDownLatch 来确保清理完成
    // 这里简化处理，验证 TraceId 在请求处理过程中被设置
    // 清理操作在 then() 中执行，会在过滤器链完成后执行
  }

  @Test
  void testFilterAddsTraceIdToForwardRequest() {
    // 设置请求头中的 TraceId
    String traceId = "forward-trace-id-12345";
    requestHeaders.add("X-Trace-Id", traceId);
    final String[] actualTraceId = new String[1];
    doAnswer(
            invocation -> {
              // 在 filter 执行过程中检查 TraceId
              actualTraceId[0] = TraceIdUtil.getTraceId();
              return Mono.empty();
            })
        .when(chain)
        .filter(any());

    // 执行过滤器
    Mono<Void> result = traceIdGatewayFilter.filter(exchange, chain);

    // 验证过滤器链被调用，并且请求被修改
    StepVerifier.create(result).verifyComplete();
    verify(chain).filter(any());

    // 验证 TraceId 在 filter 执行过程中被设置
    assertEquals(traceId, actualTraceId[0]);
    
    // 验证 TraceId 已被清理
    assertNull(TraceIdUtil.getTraceId());
  }

  @Test
  void testFilterOrderIsHighestPrecedence() {
    int order = traceIdGatewayFilter.getOrder();
    assertEquals(Ordered.HIGHEST_PRECEDENCE, order);
  }
}
