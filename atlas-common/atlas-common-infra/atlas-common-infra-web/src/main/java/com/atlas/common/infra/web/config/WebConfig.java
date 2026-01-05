/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.config;

import com.atlas.common.infra.web.filter.TraceIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Web 配置类
 *
 * <p>配置 Web 相关的 Filter、Interceptor 等组件。
 *
 * <p>主要配置：
 *
 * <ul>
 *   <li>TraceId Filter：在请求的最早阶段设置 TraceId
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>
 * // TraceId Filter 会自动注册，无需手动配置
 * // 如果需要自定义配置，可以创建自己的 WebConfig
 * </pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Configuration
public class WebConfig {

  /**
   * 注册 TraceId Filter
   *
   * <p>配置 Filter 的执行顺序和 URL 匹配模式。
   *
   * <p>配置项：
   *
   * <ul>
   *   <li>执行顺序：{@code Ordered.HIGHEST_PRECEDENCE}，确保在其他 Filter 之前执行
   *   <li>URL 模式：{@code /*}，匹配所有请求
   *   <li>响应头：默认不添加 TraceId 到响应头，可通过 {@code setAddResponseHeader(true)} 启用
   * </ul>
   *
   * @return FilterRegistrationBean
   */
  @Bean
  public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
    FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
    TraceIdFilter filter = new TraceIdFilter();
    registration.setFilter(filter);
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    registration.addUrlPatterns("/*");
    // 默认不添加响应头，如需启用可设置为 true
    // filter.setAddResponseHeader(true);
    return registration;
  }
}

