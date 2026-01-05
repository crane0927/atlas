/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

/**
 * 统一 API 响应包装类
 *
 * <p>用于封装所有 HTTP 接口的响应数据，提供统一的响应格式。 支持泛型，可以包装任意类型的数据。
 *
 * <p>成功响应：code = "000000", message = "操作成功", data = 响应数据 失败响应：code = 错误码, message = 错误消息, data =
 * null
 *
 * @param <T> 响应数据类型
 * @author Atlas Team
 * @date 2026-01-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

  /**
   * 状态码
   *
   * <p>成功响应为 "000000"，失败响应为具体的错误码（6位数字）
   */
  private String code;

  /**
   * 消息
   *
   * <p>成功响应为 "操作成功"，失败响应为错误消息
   */
  private String message;

  /**
   * 响应数据
   *
   * <p>成功响应包含数据对象，失败响应为 null
   */
  private T data;

  /**
   * 时间戳（毫秒）
   *
   * <p>响应创建的时间戳，用于问题追踪和日志分析
   */
  private Long timestamp;

  /**
   * 链路追踪 ID
   *
   * <p>用于分布式追踪，关联一次请求在整个微服务系统中的完整调用链 如果未设置，将从 MDC 中自动获取
   */
  private String traceId;

  /**
   * 创建成功响应
   *
   * @param data 响应数据
   * @param <T> 响应数据类型
   * @return Result 对象
   */
  public static <T> Result<T> success(T data) {
    return Result.<T>builder()
        .code("000000")
        .message("操作成功")
        .data(data)
        .timestamp(System.currentTimeMillis())
        .traceId(getTraceIdFromMDC())
        .build();
  }

  /**
   * 创建成功响应（自定义消息）
   *
   * @param message 自定义消息
   * @param data 响应数据
   * @param <T> 响应数据类型
   * @return Result 对象
   */
  public static <T> Result<T> success(String message, T data) {
    return Result.<T>builder()
        .code("000000")
        .message(message)
        .data(data)
        .timestamp(System.currentTimeMillis())
        .traceId(getTraceIdFromMDC())
        .build();
  }

  /**
   * 创建失败响应
   *
   * @param code 错误码
   * @param message 错误消息
   * @param <T> 响应数据类型
   * @return Result 对象
   */
  public static <T> Result<T> error(String code, String message) {
    return Result.<T>builder()
        .code(code)
        .message(message)
        .data(null)
        .timestamp(System.currentTimeMillis())
        .traceId(getTraceIdFromMDC())
        .build();
  }

  /**
   * 创建失败响应（带数据）
   *
   * @param code 错误码
   * @param message 错误消息
   * @param data 错误数据（可选）
   * @param <T> 响应数据类型
   * @return Result 对象
   */
  public static <T> Result<T> error(String code, String message, T data) {
    return Result.<T>builder()
        .code(code)
        .message(message)
        .data(data)
        .timestamp(System.currentTimeMillis())
        .traceId(getTraceIdFromMDC())
        .build();
  }

  /**
   * 判断是否成功
   *
   * @return true 表示成功，false 表示失败
   */
  public boolean isSuccess() {
    return "000000".equals(code);
  }

  /**
   * 从 MDC 获取 TraceId
   *
   * @return TraceId，如果不存在则返回 null
   */
  private static String getTraceIdFromMDC() {
    return MDC.get("traceId");
  }
}
