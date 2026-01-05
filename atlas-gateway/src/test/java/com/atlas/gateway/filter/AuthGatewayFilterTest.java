/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.common.infra.logging.trace.TraceIdUtil;
import com.atlas.gateway.config.GatewayProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * AuthGatewayFilter 单元测试
 *
 * @author Atlas Team
 */
class AuthGatewayFilterTest {

  private AuthGatewayFilter authGatewayFilter;
  private GatewayProperties gatewayProperties;
  private TokenValidator tokenValidator;
  private ServerWebExchange exchange;
  private GatewayFilterChain chain;
  private ServerHttpRequest request;
  private ServerHttpResponse response;
  private HttpHeaders requestHeaders;
  private HttpHeaders responseHeaders;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    gatewayProperties = new GatewayProperties();
    tokenValidator = mock(TokenValidator.class);
    objectMapper = new ObjectMapper();
    authGatewayFilter =
        new AuthGatewayFilter(gatewayProperties, tokenValidator, objectMapper);

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
    when(response.bufferFactory()).thenReturn(new DefaultDataBufferFactory());
    when(response.writeWith(any())).thenReturn(Mono.empty());
    when(chain.filter(any())).thenReturn(Mono.empty());
  }

  @AfterEach
  void tearDown() {
    // 清理 TraceId，避免影响其他测试
    TraceIdUtil.clear();
  }

  @Test
  void testFilterOrder() {
    assertEquals(Ordered.HIGHEST_PRECEDENCE + 1, authGatewayFilter.getOrder());
  }

  @Test
  void testFilterWithWhitelistedPath() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 配置白名单
    GatewayProperties.WhitelistConfig whitelist = gatewayProperties.getWhitelist();
    whitelist.setEnabled(true);
    whitelist.setPaths(Arrays.asList("/health/**", "/mock/**"));

    // 设置请求路径为白名单路径
    when(request.getURI()).thenReturn(java.net.URI.create("http://localhost:8080/health/test"));

    // 执行过滤器
    Mono<Void> result = authGatewayFilter.filter(exchange, chain);

    // 验证过滤器链被调用（白名单路径直接放行）
    StepVerifier.create(result).verifyComplete();
    verify(chain).filter(exchange);
  }

  @Test
  void testFilterWithNonWhitelistedPathAndTokenValid() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 配置白名单
    GatewayProperties.WhitelistConfig whitelist = gatewayProperties.getWhitelist();
    whitelist.setEnabled(true);
    whitelist.setPaths(Arrays.asList("/health/**"));

    // 设置请求路径为非白名单路径
    when(request.getURI()).thenReturn(java.net.URI.create("http://localhost:8080/api/user"));

    // Token 校验通过
    when(tokenValidator.validate(any())).thenReturn(true);

    // 执行过滤器
    Mono<Void> result = authGatewayFilter.filter(exchange, chain);

    // 验证过滤器链被调用（Token 校验通过）
    StepVerifier.create(result).verifyComplete();
    verify(chain).filter(exchange);
    verify(tokenValidator).validate(any());
  }

  @Test
  void testFilterWithNonWhitelistedPathAndTokenInvalid() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 配置白名单
    GatewayProperties.WhitelistConfig whitelist = gatewayProperties.getWhitelist();
    whitelist.setEnabled(true);
    whitelist.setPaths(Arrays.asList("/health/**"));

    // 设置请求路径为非白名单路径
    when(request.getURI()).thenReturn(java.net.URI.create("http://localhost:8080/api/user"));

    // Token 校验失败
    when(tokenValidator.validate(any())).thenReturn(false);

    // 执行过滤器
    Mono<Void> result = authGatewayFilter.filter(exchange, chain);

    // 验证返回错误响应
    StepVerifier.create(result).verifyComplete();
    verify(response).setStatusCode(org.springframework.http.HttpStatus.OK);
    verify(response.getHeaders()).setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    verify(response).writeWith(any());
    // 验证过滤器链未被调用
    verify(chain, org.mockito.Mockito.never()).filter(any());
  }

  @Test
  void testFilterWithWhitelistDisabled() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 禁用白名单
    GatewayProperties.WhitelistConfig whitelist = gatewayProperties.getWhitelist();
    whitelist.setEnabled(false);
    whitelist.setPaths(Arrays.asList("/health/**"));

    // 设置请求路径
    when(request.getURI()).thenReturn(java.net.URI.create("http://localhost:8080/health/test"));

    // Token 校验通过
    when(tokenValidator.validate(any())).thenReturn(true);

    // 执行过滤器
    Mono<Void> result = authGatewayFilter.filter(exchange, chain);

    // 验证 Token 校验被调用（白名单禁用，所有请求都需要 Token 校验）
    StepVerifier.create(result).verifyComplete();
    verify(tokenValidator).validate(any());
    verify(chain).filter(exchange);
  }

  @Test
  void testFilterWithEmptyWhitelist() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 配置空白名单
    GatewayProperties.WhitelistConfig whitelist = gatewayProperties.getWhitelist();
    whitelist.setEnabled(true);
    whitelist.setPaths(Arrays.asList());

    // 设置请求路径
    when(request.getURI()).thenReturn(java.net.URI.create("http://localhost:8080/api/user"));

    // Token 校验通过
    when(tokenValidator.validate(any())).thenReturn(true);

    // 执行过滤器
    Mono<Void> result = authGatewayFilter.filter(exchange, chain);

    // 验证 Token 校验被调用
    StepVerifier.create(result).verifyComplete();
    verify(tokenValidator).validate(any());
    verify(chain).filter(exchange);
  }

  @Test
  void testFilterWithWhitelistPathPattern() {
    // 设置 TraceId
    TraceIdUtil.setTraceId("test-trace-id-12345");

    // 配置白名单（使用通配符）
    GatewayProperties.WhitelistConfig whitelist = gatewayProperties.getWhitelist();
    whitelist.setEnabled(true);
    whitelist.setPaths(Arrays.asList("/api/public/**", "/health/**"));

    // 测试匹配 /api/public/test
    when(request.getURI()).thenReturn(java.net.URI.create("http://localhost:8080/api/public/test"));

    // 执行过滤器
    Mono<Void> result = authGatewayFilter.filter(exchange, chain);

    // 验证过滤器链被调用（匹配白名单）
    StepVerifier.create(result).verifyComplete();
    verify(chain).filter(exchange);
  }

  @Test
  void testDefaultTokenValidator() {
    DefaultTokenValidator defaultTokenValidator = new DefaultTokenValidator();
    ServerHttpRequest request = mock(ServerHttpRequest.class);
    when(request.getURI()).thenReturn(java.net.URI.create("http://localhost:8080/api/test"));

    // 验证占位实现默认返回 true
    boolean result = defaultTokenValidator.validate(request);
    assertEquals(true, result);
  }
}

