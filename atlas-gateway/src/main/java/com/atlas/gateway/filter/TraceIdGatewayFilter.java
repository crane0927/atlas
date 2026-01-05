/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.filter;

import com.atlas.common.infra.logging.trace.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * TraceId Gateway 过滤器
 *
 * <p>自动处理 TraceId，确保所有经过 Gateway 的请求都有 TraceId，并能够正确传递到后端服务和响应中。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>从请求头 {@code X-Trace-Id} 获取 TraceId
 *   <li>如果请求头中没有 TraceId，自动生成新的 TraceId
 *   <li>将 TraceId 设置到 {@link TraceIdUtil}（ThreadLocal 和 MDC），供业务代码和日志使用
 *   <li>将 TraceId 添加到转发请求的请求头，确保后端服务能够获取 TraceId
 *   <li>将 TraceId 添加到响应头，确保客户端能够获取 TraceId
 *   <li>请求结束后自动清理 TraceId，避免内存泄漏
 * </ul>
 *
 * <p>执行顺序：
 *
 * <ul>
 *   <li>执行顺序：{@code Ordered.HIGHEST_PRECEDENCE}，确保在其他过滤器之前执行
 *   <li>这样可以确保 TraceId 在所有业务逻辑之前就已经设置好
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // TraceIdGatewayFilter 会自动注册，无需手动配置
 * // 客户端请求时，如果没有 X-Trace-Id 请求头，Gateway 会自动生成
 * // 如果有 X-Trace-Id 请求头，Gateway 会使用该 TraceId
 * // TraceId 会自动传递到后端服务和响应中
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class TraceIdGatewayFilter implements GlobalFilter, Ordered {

  /** TraceId 请求头名称 */
  private static final String TRACE_ID_HEADER = "X-Trace-Id";

  /**
   * 过滤请求，设置 TraceId
   *
   * <p>处理流程：
   *
   * <ol>
   *   <li>从请求头 {@code X-Trace-Id} 获取 TraceId
   *   <li>如果请求头中没有 TraceId，调用 {@link TraceIdUtil#generate()} 生成新的 TraceId
   *   <li>调用 {@link TraceIdUtil#setTraceId(String)} 设置 TraceId（设置到 ThreadLocal 和 MDC）
   *   <li>将 TraceId 添加到转发请求的请求头（修改 ServerHttpRequest）
   *   <li>将 TraceId 添加到响应头（修改 ServerHttpResponse）
   *   <li>继续执行过滤器链
   *   <li>请求结束后清理 TraceId（在 {@code then()} 中调用 {@link TraceIdUtil#clear()}）
   * </ol>
   *
   * @param exchange 服务器 Web 交换对象
   * @param chain 网关过滤器链
   * @return Mono<Void> 响应式结果
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    // 从请求头获取 TraceId
    String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);

    // 如果请求头中没有 TraceId，生成新的 TraceId
    if (traceId == null || traceId.isEmpty()) {
      traceId = TraceIdUtil.generate();
      log.debug("请求头中没有 TraceId，自动生成: {}", traceId);
    } else {
      log.debug("从请求头获取 TraceId: {}", traceId);
    }

    // 设置 TraceId 到 ThreadLocal 和 MDC
    TraceIdUtil.setTraceId(traceId);

    // 将 TraceId 添加到转发请求的请求头（修改 ServerHttpRequest）
    ServerHttpRequest modifiedRequest =
        request
            .mutate()
            .header(TRACE_ID_HEADER, traceId)
            .build();

    // 将 TraceId 添加到响应头（修改 ServerHttpResponse）
    exchange
        .getResponse()
        .getHeaders()
        .add(TRACE_ID_HEADER, traceId);

    // 使用修改后的请求继续执行过滤器链
    ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

    // 继续执行过滤器链，并在请求结束后清理 TraceId
    return chain
        .filter(modifiedExchange)
        .then(
            Mono.fromRunnable(
                () -> {
                  TraceIdUtil.clear();
                  log.debug("请求结束，清理 TraceId: {}", traceId);
                }));
  }

  /**
   * 获取过滤器执行顺序
   *
   * <p>返回 {@code Ordered.HIGHEST_PRECEDENCE}，确保在其他过滤器之前执行。
   *
   * @return 执行顺序
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}

