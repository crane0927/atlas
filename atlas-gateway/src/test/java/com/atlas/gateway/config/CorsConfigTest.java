/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.atlas.gateway.config.GatewayProperties.CorsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.cors.reactive.CorsWebFilter;

/**
 * CorsConfig 单元测试
 *
 * @author Atlas Team
 */
class CorsConfigTest {

  private GatewayProperties gatewayProperties;
  private CorsConfig corsConfig;

  @BeforeEach
  void setUp() {
    gatewayProperties = new GatewayProperties();
    corsConfig = new CorsConfig(gatewayProperties);
  }

  @Test
  void testCorsConfigCreation() {
    assertNotNull(corsConfig);
  }

  @Test
  void testCorsWebFilterBeanCreation() {
    CorsWebFilter corsWebFilter = corsConfig.corsWebFilter();
    assertNotNull(corsWebFilter);
  }

  @Test
  void testCorsWebFilterWithDefaultConfiguration() {
    // 使用默认配置
    CorsWebFilter corsWebFilter = corsConfig.corsWebFilter();
    assertNotNull(corsWebFilter);
  }

  @Test
  void testCorsWebFilterWithCustomConfiguration() {
    // 设置自定义 CORS 配置
    GatewayProperties.CorsConfig corsProperties = gatewayProperties.getCors();
    corsProperties.setAllowedOrigins("http://localhost:3000,http://localhost:3001");
    corsProperties.setAllowedMethods("GET,POST");
    corsProperties.setAllowedHeaders("Content-Type,Authorization");
    corsProperties.setAllowCredentials(false);
    corsProperties.setMaxAge(1800);

    // 刷新配置
    corsConfig.refreshCorsConfig();

    // 验证过滤器可以正常创建
    CorsWebFilter corsWebFilter = corsConfig.corsWebFilter();
    assertNotNull(corsWebFilter);
  }

  @Test
  void testRefreshCorsConfig() {
    // 初始配置
    GatewayProperties.CorsConfig corsProperties = gatewayProperties.getCors();
    corsProperties.setAllowedOrigins("*");
    corsProperties.setAllowedMethods("GET,POST,PUT,DELETE,OPTIONS");

    // 刷新配置
    corsConfig.refreshCorsConfig();

    // 更新配置
    corsProperties.setAllowedOrigins("http://localhost:3000");
    corsProperties.setAllowedMethods("GET,POST");

    // 再次刷新配置
    corsConfig.refreshCorsConfig();

    // 验证过滤器可以正常创建
    CorsWebFilter corsWebFilter = corsConfig.corsWebFilter();
    assertNotNull(corsWebFilter);
  }

  @Test
  void testCorsWebFilterWithWildcardOrigins() {
    // 设置通配符源
    GatewayProperties.CorsConfig corsProperties = gatewayProperties.getCors();
    corsProperties.setAllowedOrigins("*");

    // 刷新配置
    corsConfig.refreshCorsConfig();

    // 验证过滤器可以正常创建
    CorsWebFilter corsWebFilter = corsConfig.corsWebFilter();
    assertNotNull(corsWebFilter);
  }

  @Test
  void testCorsWebFilterWithWildcardHeaders() {
    // 设置通配符请求头
    GatewayProperties.CorsConfig corsProperties = gatewayProperties.getCors();
    corsProperties.setAllowedHeaders("*");

    // 刷新配置
    corsConfig.refreshCorsConfig();

    // 验证过滤器可以正常创建
    CorsWebFilter corsWebFilter = corsConfig.corsWebFilter();
    assertNotNull(corsWebFilter);
  }
}

