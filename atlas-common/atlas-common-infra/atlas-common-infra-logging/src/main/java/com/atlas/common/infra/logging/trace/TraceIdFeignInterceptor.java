/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.trace;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.lang.NonNull;

/**
 * TraceId Feign 拦截器
 *
 * <p>自动将当前线程的 TraceId 添加到 Feign 请求头，实现微服务调用链追踪。
 *
 * <p>工作流程：
 *
 * <ol>
 *   <li>Feign 调用前，从 TraceIdUtil 获取当前线程的 TraceId</li>
 *   <li>将 TraceId 添加到请求头 {@code X-Trace-Id}</li>
 *   <li>下游服务可以通过 TraceIdInterceptor 获取 TraceId，实现链路追踪</li>
 * </ol>
 *
 * <p>配置示例：
 *
 * <pre>{@code
 * @Configuration
 * public class FeignConfig {
 *     @Bean
 *     public TraceIdFeignInterceptor traceIdFeignInterceptor() {
 *         return new TraceIdFeignInterceptor();
 *     }
 * }
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class TraceIdFeignInterceptor implements RequestInterceptor {

  /** TraceId 请求头名称 */
  private static final String TRACE_ID_HEADER = "X-Trace-Id";

  /**
   * 应用拦截器逻辑
   *
   * <p>从 TraceIdUtil 获取当前线程的 TraceId，并添加到 Feign 请求头。
   *
   * @param template Feign 请求模板
   */
  @Override
  public void apply(@NonNull RequestTemplate template) {
    // 从 TraceIdUtil 获取当前线程的 TraceId
    String traceId = TraceIdUtil.getTraceId();

    // 如果 TraceId 存在，则添加到请求头
    if (traceId != null && !traceId.isEmpty()) {
      template.header(TRACE_ID_HEADER, traceId);
    }
  }
}

