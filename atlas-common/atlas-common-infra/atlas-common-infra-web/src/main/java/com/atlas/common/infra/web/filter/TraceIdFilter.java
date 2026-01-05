/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.filter;

import com.atlas.common.infra.logging.trace.TraceIdUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * TraceId HTTP Filter
 *
 * <p>在请求的最早阶段设置 TraceId，确保所有请求都有 TraceId。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>从 HTTP 请求头 {@code X-Trace-Id} 获取 TraceId
 *   <li>如果请求头中没有 TraceId，则自动生成
 *   <li>将 TraceId 设置到 {@link TraceIdUtil}（复用 {@code atlas-common-infra-logging} 模块的工具类）
 *   <li>可选地在响应头中添加 TraceId
 *   <li>请求结束后自动清理 TraceId，避免内存泄漏
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>
 * // 在 WebConfig 中配置
 * @Bean
 * public FilterRegistrationBean&lt;TraceIdFilter&gt; traceIdFilter() {
 *     FilterRegistrationBean&lt;TraceIdFilter&gt; registration = new FilterRegistrationBean&lt;&gt;();
 *     registration.setFilter(new TraceIdFilter());
 *     registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
 *     registration.addUrlPatterns("/*");
 *     return registration;
 * }
 * </pre>
 *
 * <p>注意：此 Filter 执行顺序要早于其他 Filter，确保 TraceId 在所有组件之前设置。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
public class TraceIdFilter implements Filter {

  /** TraceId 请求头名称 */
  private static final String TRACE_ID_HEADER = "X-Trace-Id";

  /** 是否在响应头中添加 TraceId */
  private boolean addResponseHeader = false;

  /**
   * 设置是否在响应头中添加 TraceId
   *
   * @param addResponseHeader 是否添加响应头
   */
  public void setAddResponseHeader(boolean addResponseHeader) {
    this.addResponseHeader = addResponseHeader;
  }

  /**
   * 过滤请求，设置 TraceId
   *
   * @param request HTTP 请求
   * @param response HTTP 响应
   * @param chain Filter 链
   * @throws IOException 如果 IO 操作失败
   * @throws ServletException 如果 Servlet 操作失败
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    try {
      // 从请求头获取 TraceId
      String traceId = httpRequest.getHeader(TRACE_ID_HEADER);

      // 如果请求头中没有 TraceId，则自动生成
      if (traceId == null || traceId.isEmpty()) {
        traceId = TraceIdUtil.generate();
        log.debug("生成新的 TraceId: {}", traceId);
      }

      // 设置 TraceId 到 TraceIdUtil
      TraceIdUtil.setTraceId(traceId);

      // 可选地在响应头中添加 TraceId
      if (addResponseHeader) {
        httpResponse.setHeader(TRACE_ID_HEADER, traceId);
      }

      // 继续执行 Filter 链
      chain.doFilter(request, response);
    } finally {
      // 请求结束后清理 TraceId，避免内存泄漏
      TraceIdUtil.clear();
    }
  }
}
