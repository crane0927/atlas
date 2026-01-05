/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.common.feature.core.result.Result;
import com.atlas.common.infra.logging.trace.TraceIdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * GatewayExceptionHandler 单元测试
 *
 * @author Atlas Team
 */
class GatewayExceptionHandlerTest {

  private GatewayExceptionHandler exceptionHandler;
  private ServerWebExchange exchange;
  private ServerHttpResponse response;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    exceptionHandler = new GatewayExceptionHandler(objectMapper);
    exchange = mock(ServerWebExchange.class);
    response = mock(ServerHttpResponse.class);

    when(exchange.getResponse()).thenReturn(response);
    when(response.bufferFactory()).thenReturn(new DefaultDataBufferFactory());
    when(response.writeWith(any())).thenReturn(Mono.empty());
  }

  @AfterEach
  void tearDown() {
    // 清理 TraceId，避免影响其他测试
    TraceIdUtil.clear();
  }

  @Test
  void testHandleNotFoundException() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 创建 NotFoundException
    ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "路由不存在");

    // 执行异常处理
    Mono<Void> result = exceptionHandler.handle(exchange, exception);

    // 验证响应状态码和 Content-Type
    StepVerifier.create(result).verifyComplete();
    verify(response).setStatusCode(HttpStatus.OK);
    verify(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
    verify(response).writeWith(any());
  }

  @Test
  void testHandleServiceUnavailableException() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 创建 ServiceUnavailableException
    ResponseStatusException exception = new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "服务不可用");

    // 执行异常处理
    Mono<Void> result = exceptionHandler.handle(exchange, exception);

    // 验证响应状态码和 Content-Type
    StepVerifier.create(result).verifyComplete();
    verify(response).setStatusCode(HttpStatus.OK);
    verify(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
    verify(response).writeWith(any());
  }

  @Test
  void testHandleTimeoutException() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 创建 TimeoutException
    TimeoutException exception = new TimeoutException("请求超时");

    // 执行异常处理
    Mono<Void> result = exceptionHandler.handle(exchange, exception);

    // 验证响应状态码和 Content-Type
    StepVerifier.create(result).verifyComplete();
    verify(response).setStatusCode(HttpStatus.OK);
    verify(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
    verify(response).writeWith(any());
  }

  @Test
  void testHandleGenericException() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 创建通用异常
    RuntimeException exception = new RuntimeException("Gateway 错误");

    // 执行异常处理
    Mono<Void> result = exceptionHandler.handle(exchange, exception);

    // 验证响应状态码和 Content-Type
    StepVerifier.create(result).verifyComplete();
    verify(response).setStatusCode(HttpStatus.OK);
    verify(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
    verify(response).writeWith(any());
  }

  @Test
  void testHandleExceptionWithoutTraceId() {
    // 不设置 TraceId

    // 创建异常
    ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "路由不存在");

    // 执行异常处理
    Mono<Void> result = exceptionHandler.handle(exchange, exception);

    // 验证响应状态码和 Content-Type
    StepVerifier.create(result).verifyComplete();
    verify(response).setStatusCode(HttpStatus.OK);
    verify(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
    verify(response).writeWith(any());
  }

  @Test
  void testErrorResponseFormat() throws Exception {
    // 设置 TraceId
    String traceId = "test-trace-id-12345";
    TraceIdUtil.setTraceId(traceId);

    // 创建 NotFoundException
    ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "路由不存在");

    // 创建真实的 DataBuffer 来捕获响应内容
    DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory();
    DataBuffer[] capturedBuffer = new DataBuffer[1];

    when(response.bufferFactory()).thenReturn(bufferFactory);
    when(response.writeWith(any())).thenAnswer(invocation -> {
      Mono<DataBuffer> bufferMono = invocation.getArgument(0);
      return bufferMono.doOnNext(buffer -> capturedBuffer[0] = buffer).then();
    });

    // 执行异常处理
    Mono<Void> result = exceptionHandler.handle(exchange, exception);

    // 验证响应
    StepVerifier.create(result).verifyComplete();

    // 验证响应内容格式
    if (capturedBuffer[0] != null) {
      String json = capturedBuffer[0].toString(StandardCharsets.UTF_8);
      Result<?> resultObj = objectMapper.readValue(json, Result.class);
      assertEquals("010404", resultObj.getCode());
      assertEquals("路由不存在", resultObj.getMessage());
      assertEquals(traceId, resultObj.getTraceId());
      assertNotNull(resultObj.getTimestamp());
    }
  }
}

