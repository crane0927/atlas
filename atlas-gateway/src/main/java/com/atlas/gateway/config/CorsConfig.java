/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.config;

import com.atlas.gateway.config.GatewayProperties.CorsConfig;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * CORS 配置类
 *
 * <p>配置 Spring Cloud Gateway CORS 跨域支持，支持从 Nacos Config 读取 CORS 配置并动态更新。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>从 {@link GatewayProperties} 读取 CORS 配置
 *   <li>配置 CORS 允许的源、方法、请求头等
 *   <li>支持预检请求（OPTIONS）处理
 *   <li>支持 CORS 配置动态更新（监听 Nacos Config 配置变更）
 * </ul>
 *
 * <p>配置示例：
 *
 * <pre>{@code
 * atlas:
 *   gateway:
 *     cors:
 *       allowed-origins: "*"
 *       allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
 *       allowed-headers: "*"
 *       allow-credentials: true
 *       max-age: 3600
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class CorsConfig {

  private final GatewayProperties gatewayProperties;
  private final UrlBasedCorsConfigurationSource corsConfigurationSource;

  public CorsConfig(GatewayProperties gatewayProperties) {
    this.gatewayProperties = gatewayProperties;
    this.corsConfigurationSource = new UrlBasedCorsConfigurationSource();
    updateCorsConfiguration();
  }

  /**
   * 创建 CORS Web 过滤器
   *
   * <p>从 {@link GatewayProperties} 读取 CORS 配置，创建 {@link CorsWebFilter} Bean。
   *
   * <p>CORS 配置支持通过 Nacos Config 动态更新，配置变更后会自动更新配置源。
   *
   * @return CORS Web 过滤器
   */
  @Bean
  public CorsWebFilter corsWebFilter() {
    return new CorsWebFilter(corsConfigurationSource);
  }

  /**
   * 更新 CORS 配置
   *
   * <p>根据 {@link GatewayProperties} 中的 CORS 配置更新配置源。
   */
  private void updateCorsConfiguration() {
    CorsConfig corsConfig = gatewayProperties.getCors();
    org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();

    // 配置允许的源
    if (corsConfig.getAllowedOrigins() != null) {
      if ("*".equals(corsConfig.getAllowedOrigins())) {
        config.addAllowedOriginPattern("*");
      } else {
        List<String> origins =
            Arrays.asList(corsConfig.getAllowedOrigins().split(","));
        origins.forEach(origin -> config.addAllowedOrigin(origin.trim()));
      }
    }

    // 配置允许的 HTTP 方法
    if (corsConfig.getAllowedMethods() != null) {
      List<String> methods =
          Arrays.asList(corsConfig.getAllowedMethods().split(","));
      methods.forEach(
          method -> {
            try {
              config.addAllowedMethod(HttpMethod.valueOf(method.trim()));
            } catch (IllegalArgumentException e) {
              log.warn("无效的 HTTP 方法: {}", method.trim());
            }
          });
    }

    // 配置允许的请求头
    if (corsConfig.getAllowedHeaders() != null) {
      if ("*".equals(corsConfig.getAllowedHeaders())) {
        config.addAllowedHeader("*");
      } else {
        List<String> headers =
            Arrays.asList(corsConfig.getAllowedHeaders().split(","));
        headers.forEach(header -> config.addAllowedHeader(header.trim()));
      }
    }

    // 配置是否允许携带凭证
    if (corsConfig.getAllowCredentials() != null) {
      config.setAllowCredentials(corsConfig.getAllowCredentials());
    }

    // 配置预检请求缓存时间
    if (corsConfig.getMaxAge() != null && corsConfig.getMaxAge() >= 0) {
      config.setMaxAge(corsConfig.getMaxAge());
    }

    // 配置对所有路径生效
    corsConfigurationSource.registerCorsConfiguration("/**", config);

    log.debug(
        "更新 CORS 配置: allowedOrigins={}, allowedMethods={}, allowedHeaders={}, allowCredentials={}, maxAge={}",
        corsConfig.getAllowedOrigins(),
        corsConfig.getAllowedMethods(),
        corsConfig.getAllowedHeaders(),
        corsConfig.getAllowCredentials(),
        corsConfig.getMaxAge());
  }

  /**
   * 刷新 CORS 配置
   *
   * <p>从 {@link GatewayProperties} 重新读取 CORS 配置并更新配置源。
   *
   * <p>此方法可以在 Nacos Config 配置变更时调用，实现 CORS 配置动态更新。
   *
   * <p>注意：通过更新配置源，CorsWebFilter 会自动使用新的配置，无需重新创建 Bean。
   */
  public void refreshCorsConfig() {
    log.info("开始刷新 CORS 配置...");
    try {
      updateCorsConfiguration();
      log.info("CORS 配置刷新成功");
    } catch (Exception e) {
      log.error("CORS 配置刷新失败", e);
    }
  }
}

