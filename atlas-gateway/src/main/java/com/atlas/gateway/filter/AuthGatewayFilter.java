/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.filter;

import com.atlas.common.feature.core.result.Result;
import com.atlas.common.infra.logging.trace.TraceIdUtil;
import com.atlas.gateway.config.GatewayProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 鉴权 Gateway 过滤器
 *
 * <p>实现 Gateway 鉴权控制，支持白名单和 Token 校验扩展点。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>白名单路径匹配：使用 Ant 风格路径匹配器匹配白名单路径
 *   <li>白名单放行：白名单路径的请求直接放行，无需 Token 校验
 *   <li>Token 校验：非白名单路径的请求会触发 Token 校验
 *   <li>Token 校验扩展点：通过 {@link TokenValidator} 接口提供扩展点
 *   <li>占位实现：Token 校验占位实现默认放行，便于后续扩展
 *   <li>动态配置：白名单配置支持通过 Nacos Config 动态更新
 * </ul>
 *
 * <p>执行顺序：
 *
 * <ul>
 *   <li>执行顺序：{@code Ordered.HIGHEST_PRECEDENCE + 1}，确保在 TraceId 过滤器之后执行
 *   <li>这样可以确保 TraceId 已经设置好，便于日志追踪
 * </ul>
 *
 * <p>处理流程：
 *
 * <ol>
 *   <li>检查白名单是否启用
 *   <li>如果启用，检查请求路径是否匹配白名单
 *   <li>如果匹配白名单，直接放行
 *   <li>如果不匹配白名单，调用 {@link TokenValidator#validate(ServerHttpRequest)} 进行 Token 校验
 *   <li>如果 Token 校验失败，返回统一错误格式（错误码：013001）
 * </ol>
 *
 * <p>配置示例：
 *
 * <pre>{@code
 * atlas:
 *   gateway:
 *     whitelist:
 *       enabled: true
 *       paths:
 *         - /health/**
 *         - /mock/**
 *         - /api/public/**
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class AuthGatewayFilter implements GlobalFilter, Ordered {

  /** Token 校验失败错误码 */
  private static final String AUTH_ERROR_CODE = "013001";

  /** Token 校验失败错误消息 */
  private static final String AUTH_ERROR_MESSAGE = "Token 校验失败";

  private final GatewayProperties gatewayProperties;
  private final TokenValidator tokenValidator;
  private final AntPathMatcher pathMatcher;
  private final ObjectMapper objectMapper;

  @Autowired
  public AuthGatewayFilter(
      GatewayProperties gatewayProperties,
      TokenValidator tokenValidator,
      ObjectMapper objectMapper) {
    this.gatewayProperties = gatewayProperties;
    this.tokenValidator = tokenValidator;
    this.pathMatcher = new AntPathMatcher();
    this.objectMapper = objectMapper != null ? objectMapper : new com.fasterxml.jackson.databind.ObjectMapper();
  }

  /**
   * 过滤请求，实现鉴权控制
   *
   * <p>处理流程：
   *
   * <ol>
   *   <li>检查白名单是否启用
   *   <li>如果启用，检查请求路径是否匹配白名单
   *   <li>如果匹配白名单，直接放行
   *   <li>如果不匹配白名单，调用 Token 校验
   *   <li>如果 Token 校验失败，返回统一错误格式
   * </ol>
   *
   * @param exchange 服务器 Web 交换对象
   * @param chain 网关过滤器链
   * @return Mono<Void> 响应式结果
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getURI().getPath();

    // 检查白名单是否启用
    GatewayProperties.WhitelistConfig whitelist = gatewayProperties.getWhitelist();
    if (whitelist.getEnabled() != null && whitelist.getEnabled()) {
      // 检查请求路径是否匹配白名单
      if (isWhitelisted(path, whitelist.getPaths())) {
        log.debug("请求路径匹配白名单，直接放行: path={}", path);
        return chain.filter(exchange);
      }
    }

    // 非白名单路径，进行 Token 校验
    log.debug("请求路径不匹配白名单，进行 Token 校验: path={}", path);
    if (tokenValidator.validate(request)) {
      log.debug("Token 校验通过，放行请求: path={}", path);
      return chain.filter(exchange);
    }

    // Token 校验失败，返回错误
    log.warn("Token 校验失败，拒绝请求: path={}", path);
    return handleAuthError(exchange);
  }

  /**
   * 检查路径是否在白名单中
   *
   * <p>使用 Ant 风格路径匹配器匹配白名单路径。
   *
   * <p>支持的匹配模式：
   *
   * <ul>
   *   <li>{@code /health/**}：匹配 /health 及其所有子路径
   *   <li>{@code /api/public/**}：匹配 /api/public 及其所有子路径
   *   <li>{@code /health}：精确匹配 /health
   * </ul>
   *
   * @param path 请求路径
   * @param whitelistPaths 白名单路径列表
   * @return true 表示路径匹配白名单，false 表示不匹配
   */
  private boolean isWhitelisted(String path, List<String> whitelistPaths) {
    if (whitelistPaths == null || whitelistPaths.isEmpty()) {
      return false;
    }

    return whitelistPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
  }

  /**
   * 处理鉴权错误
   *
   * <p>返回统一的错误响应格式，包含错误码、错误消息和 TraceId。
   *
   * @param exchange 服务器 Web 交换对象
   * @return Mono<Void> 响应式结果
   */
  private Mono<Void> handleAuthError(ServerWebExchange exchange) {
    ServerHttpResponse response = exchange.getResponse();

    // 构建错误响应
    String traceId = TraceIdUtil.getTraceId();
    Result<Void> result = Result.error(AUTH_ERROR_CODE, AUTH_ERROR_MESSAGE);
    if (traceId != null) {
      result.setTraceId(traceId);
    }

    // 设置响应状态码和 Content-Type
    response.setStatusCode(HttpStatus.OK);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    // 将错误响应序列化为 JSON
    try {
      String json = objectMapper.writeValueAsString(result);
      DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
      return response.writeWith(Mono.just(buffer));
    } catch (JsonProcessingException e) {
      log.error("序列化错误响应失败", e);
      // 如果序列化失败，返回简单的错误响应
      String fallbackJson =
          String.format(
              "{\"code\":\"%s\",\"message\":\"%s\",\"traceId\":\"%s\",\"timestamp\":%d}",
              AUTH_ERROR_CODE,
              AUTH_ERROR_MESSAGE,
              traceId != null ? traceId : "",
              System.currentTimeMillis());
      DataBuffer buffer = response.bufferFactory().wrap(fallbackJson.getBytes(StandardCharsets.UTF_8));
      return response.writeWith(Mono.just(buffer));
    }
  }

  /**
   * 获取过滤器执行顺序
   *
   * <p>返回 {@code Ordered.HIGHEST_PRECEDENCE + 1}，确保在 TraceId 过滤器之后执行。
   *
   * @return 执行顺序
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 1;
  }
}

