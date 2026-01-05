/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.logging.trace;

import org.slf4j.MDC;

/**
 * TraceId 管理工具类
 *
 * <p>提供 TraceId 的设置、获取和清理方法，使用 ThreadLocal + MDC 双重存储：
 *
 * <ul>
 *   <li>ThreadLocal：用于业务代码访问，线程隔离，性能好
 *   <li>MDC：用于日志自动输出，与 Logback 深度集成
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // 设置 TraceId
 * TraceIdUtil.setTraceId("abc123def456");
 *
 * // 获取 TraceId
 * String traceId = TraceIdUtil.getTraceId();
 *
 * // 生成并设置新的 TraceId
 * TraceIdUtil.setTraceId(TraceIdUtil.generate());
 *
 * // 清理 TraceId（请求结束后调用）
 * TraceIdUtil.clear();
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public final class TraceIdUtil {

  /** MDC 键名 */
  private static final String MDC_KEY = "traceId";

  /** ThreadLocal 存储 TraceId */
  private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

  /** 私有构造函数，防止实例化 */
  private TraceIdUtil() {
    throw new UnsupportedOperationException("工具类不允许实例化");
  }

  /**
   * 设置当前线程的 TraceId
   *
   * <p>同时设置 ThreadLocal 和 MDC，确保业务代码和日志都能访问 TraceId。
   *
   * @param traceId TraceId 字符串，不能为 null
   * @throws IllegalArgumentException 如果 traceId 为 null
   */
  public static void setTraceId(String traceId) {
    if (traceId == null) {
      throw new IllegalArgumentException("TraceId 不能为 null");
    }
    // 设置 ThreadLocal
    TRACE_ID.set(traceId);
    // 设置 MDC，供日志使用
    MDC.put(MDC_KEY, traceId);
  }

  /**
   * 获取当前线程的 TraceId
   *
   * <p>优先从 ThreadLocal 获取，如果为空则从 MDC 获取。
   *
   * @return TraceId 字符串，如果不存在则返回 null
   */
  public static String getTraceId() {
    // 优先从 ThreadLocal 获取
    String traceId = TRACE_ID.get();
    if (traceId != null) {
      return traceId;
    }
    // 如果 ThreadLocal 为空，从 MDC 获取
    return MDC.get(MDC_KEY);
  }

  /**
   * 清除当前线程的 TraceId
   *
   * <p>同时清理 ThreadLocal 和 MDC，避免内存泄漏。
   *
   * <p>注意：请求结束后必须调用此方法清理 TraceId，否则可能导致内存泄漏。
   */
  public static void clear() {
    // 清理 ThreadLocal
    TRACE_ID.remove();
    // 清理 MDC
    MDC.remove(MDC_KEY);
  }

  /**
   * 生成新的 TraceId
   *
   * <p>使用 UUID 生成 32 位 TraceId（去除连字符）。
   *
   * @return 32 位 TraceId 字符串
   */
  public static String generate() {
    return TraceIdGenerator.generateUUID();
  }
}
