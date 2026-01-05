/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.exception;

import com.atlas.common.feature.core.result.Result;
import com.atlas.common.infra.logging.trace.TraceIdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway 统一异常处理器
 *
 * <p>统一处理 Gateway 中的所有异常，返回标准的 {@link Result} 格式响应。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>捕获路由失败异常（NotFoundException），返回错误码 {@code 010404}
 *   <li>捕获服务不可用异常（ServiceUnavailableException），返回错误码 {@code 010503}
 *   <li>捕获请求超时异常（TimeoutException），返回错误码 {@code 010002}
 *   <li>捕获其他 Gateway 异常，返回错误码 {@code 010000}
 *   <li>所有错误响应使用统一的 {@link Result} 格式
 *   <li>错误响应包含错误码、错误消息、TraceId
 *   <li>错误码符合项目错误码规范（01 开头）
 * </ul>
 *
 * <p>执行顺序：
 *
 * <ul>
 *   <li>执行顺序：{@code @Order(-1)}，确保优先执行，覆盖默认的错误处理
 * </ul>
 *
 * <p>错误码定义：
 *
 * <ul>
 *   <li>{@code 010404}：路由不存在（NotFoundException）
 *   <li>{@code 010503}：服务不可用（ServiceUnavailableException）
 *   <li>{@code 010002}：请求超时（TimeoutException）
 *   <li>{@code 010000}：Gateway 其他异常
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(-1)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

  private final ObjectMapper objectMapper;

  public GatewayExceptionHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
  }

  /**
   * 处理异常
   *
   * <p>根据异常类型确定错误码和错误消息，构建统一的错误响应。
   *
   * <p>处理流程：
   *
   * <ol>
   *   <li>根据异常类型确定错误码和错误消息
   *   <li>从 {@link TraceIdUtil} 获取 TraceId
   *   <li>使用 {@link Result#error(String, String)} 构建错误响应
   *   <li>设置响应状态码为 {@code HttpStatus.OK}（统一错误格式使用 200 状态码）
   *   <li>设置响应 Content-Type 为 {@code MediaType.APPLICATION_JSON}
   *   <li>将错误响应写入响应流
   * </ol>
   *
   * @param exchange 服务器 Web 交换对象
   * @param ex 异常对象
   * @return Mono<Void> 响应式结果
   */
  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    ServerHttpResponse response = exchange.getResponse();

    // 根据异常类型确定错误码和错误消息
    String errorCode;
    String errorMessage;

    if (ex instanceof ResponseStatusException) {
      ResponseStatusException statusException = (ResponseStatusException) ex;
      HttpStatus status = statusException.getStatusCode();
      if (status == HttpStatus.NOT_FOUND) {
        // 路由不存在
        errorCode = "010404";
        errorMessage = "路由不存在";
      } else if (status == HttpStatus.SERVICE_UNAVAILABLE) {
        // 服务不可用
        errorCode = "010503";
        errorMessage = "服务不可用";
      } else {
        // 其他 HTTP 状态异常
        errorCode = "010000";
        errorMessage = statusException.getReason() != null ? statusException.getReason() : "Gateway 错误";
      }
    } else if (ex instanceof TimeoutException) {
      // 请求超时
      errorCode = "010002";
      errorMessage = "请求超时";
    } else if (ex instanceof org.springframework.cloud.gateway.support.NotFoundException) {
      // Gateway 路由不存在异常
      errorCode = "010404";
      errorMessage = "路由不存在";
    } else {
      // 其他异常
      errorCode = "010000";
      errorMessage = "Gateway 错误";
    }

    // 记录异常日志
    log.error("Gateway 异常: errorCode={}, message={}, exception={}", errorCode, errorMessage, ex.getClass().getName(), ex);

    // 构建错误响应
    String traceId = TraceIdUtil.getTraceId();
    Result<Void> result = Result.error(errorCode, errorMessage);
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
      String fallbackJson = String.format(
          "{\"code\":\"%s\",\"message\":\"%s\",\"traceId\":\"%s\",\"timestamp\":%d}",
          errorCode, errorMessage, traceId != null ? traceId : "", System.currentTimeMillis());
      DataBuffer buffer = response.bufferFactory().wrap(fallbackJson.getBytes(StandardCharsets.UTF_8));
      return response.writeWith(Mono.just(buffer));
    }
  }
}

