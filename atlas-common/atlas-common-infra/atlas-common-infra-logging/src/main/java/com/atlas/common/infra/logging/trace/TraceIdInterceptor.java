/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.logging.trace;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * TraceId HTTP 请求拦截器
 *
 * <p>自动从 HTTP 请求头获取或生成 TraceId，并在请求结束后清理。
 *
 * <p>工作流程：
 *
 * <ol>
 *   <li>请求到达时（preHandle）：从请求头 {@code X-Trace-Id} 获取 TraceId，如果不存在则自动生成
 *   <li>TraceId 设置到 ThreadLocal 和 MDC，供业务代码和日志使用
 *   <li>请求结束后（afterCompletion）：清理 TraceId，避免内存泄漏
 * </ol>
 *
 * <p>配置示例：
 *
 * <pre>{@code
 * @Configuration
 * public class WebConfig implements WebMvcConfigurer {
 *     @Override
 *     public void addInterceptors(InterceptorRegistry registry) {
 *         registry.addInterceptor(new TraceIdInterceptor())
 *                 .addPathPatterns("/**");
 *     }
 * }
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class TraceIdInterceptor implements HandlerInterceptor {

  /** TraceId 请求头名称 */
  private static final String TRACE_ID_HEADER = "X-Trace-Id";

  /**
   * 请求处理前拦截
   *
   * <p>从请求头获取 TraceId，如果不存在则自动生成，然后设置到 ThreadLocal 和 MDC。
   *
   * @param request HTTP 请求
   * @param response HTTP 响应
   * @param handler 处理器
   * @return 总是返回 true，允许请求继续处理
   */
  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    // 从请求头获取 TraceId
    String traceId = request.getHeader(TRACE_ID_HEADER);

    // 如果请求头中没有 TraceId，则自动生成
    if (traceId == null || traceId.isEmpty()) {
      traceId = TraceIdUtil.generate();
    }

    // 设置 TraceId 到 ThreadLocal 和 MDC
    TraceIdUtil.setTraceId(traceId);

    return true;
  }

  /**
   * 请求处理完成后拦截
   *
   * <p>清理 TraceId，避免内存泄漏。
   *
   * @param request HTTP 请求
   * @param response HTTP 响应
   * @param handler 处理器
   * @param ex 异常（如果有）
   */
  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      Exception ex) {
    // 清理 TraceId，避免内存泄漏
    TraceIdUtil.clear();
  }
}
