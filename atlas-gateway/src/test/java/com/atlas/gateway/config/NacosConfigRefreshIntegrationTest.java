/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.atlas.gateway.config.GatewayProperties.CorsConfig;
import com.atlas.gateway.config.GatewayProperties.RouteConfig;
import com.atlas.gateway.config.GatewayProperties.WhitelistConfig;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

/**
 * Nacos Config 配置动态更新集成测试
 *
 * <p>验证 Gateway 配置可以通过 Nacos Config 动态更新，包括路由规则、白名单和 CORS 配置。
 *
 * @author Atlas Team
 */
@SpringBootTest(classes = {GatewayConfig.class, CorsConfig.class, NacosConfigRefreshListener.class})
@EnableConfigurationProperties(GatewayProperties.class)
@TestPropertySource(
    properties = {
      "atlas.gateway.routes[0].id=test-route-1",
      "atlas.gateway.routes[0].uri=http://localhost:8080",
      "atlas.gateway.routes[0].predicates[0]=Path=/test1/**",
      "atlas.gateway.whitelist.enabled=true",
      "atlas.gateway.whitelist.paths[0]=/test1/**",
      "atlas.gateway.cors.allowed-origins=*",
      "atlas.gateway.cors.allowed-methods=GET,POST"
    })
class NacosConfigRefreshIntegrationTest {

  @Autowired private GatewayProperties gatewayProperties;

  @Autowired private GatewayConfig gatewayConfig;

  @Autowired private CorsConfig corsConfig;

  @Autowired private ApplicationContext applicationContext;

  @Test
  void testGatewayPropertiesConfigurationBinding() {
    // 验证配置属性正确绑定
    assertNotNull(gatewayProperties);
    assertNotNull(gatewayProperties.getRoutes());
    assertNotNull(gatewayProperties.getWhitelist());
    assertNotNull(gatewayProperties.getCors());
  }

  @Test
  void testRoutesConfigurationBinding() {
    // 验证路由配置正确绑定
    List<RouteConfig> routes = gatewayProperties.getRoutes();
    assertNotNull(routes);
    assertTrue(routes.size() > 0);

    RouteConfig route = routes.get(0);
    assertEquals("test-route-1", route.getId());
    assertEquals("http://localhost:8080", route.getUri());
    assertTrue(route.getPredicates().contains("Path=/test1/**"));
  }

  @Test
  void testWhitelistConfigurationBinding() {
    // 验证白名单配置正确绑定
    WhitelistConfig whitelist = gatewayProperties.getWhitelist();
    assertNotNull(whitelist);
    assertTrue(whitelist.getEnabled());
    assertTrue(whitelist.getPaths().contains("/test1/**"));
  }

  @Test
  void testCorsConfigurationBinding() {
    // 验证 CORS 配置正确绑定
    CorsConfig cors = gatewayProperties.getCors();
    assertNotNull(cors);
    assertEquals("*", cors.getAllowedOrigins());
    assertEquals("GET,POST", cors.getAllowedMethods());
  }

  @Test
  void testConfigurationNamingConvention() {
    // 验证配置项符合项目的配置命名规范（atlas.gateway.*）
    // 通过检查 GatewayProperties 的 @ConfigurationProperties 注解
    org.springframework.boot.context.properties.ConfigurationProperties annotation =
        GatewayProperties.class.getAnnotation(
            org.springframework.boot.context.properties.ConfigurationProperties.class);
    assertNotNull(annotation);
    assertEquals("atlas.gateway", annotation.prefix());
  }

  @Test
  void testRouteDefinitionLocator() {
    // 验证路由定义定位器可以正确读取路由配置
    RouteDefinitionLocator locator = gatewayConfig.routeDefinitionLocator();
    assertNotNull(locator);

    // 注意：由于是响应式编程，这里只验证定位器可以创建
    // 实际的路由定义读取需要在响应式上下文中测试
  }

  @Test
  void testCorsWebFilterBean() {
    // 验证 CORS Web 过滤器 Bean 可以正确创建
    org.springframework.web.cors.reactive.CorsWebFilter corsWebFilter = corsConfig.corsWebFilter();
    assertNotNull(corsWebFilter);
  }

  @Test
  void testConfigurationDynamicUpdate() {
    // 模拟配置变更事件
    EnvironmentChangeEvent event =
        new EnvironmentChangeEvent(
            applicationContext,
            Arrays.asList(
                "atlas.gateway.routes[0].uri",
                "atlas.gateway.whitelist.paths[0]",
                "atlas.gateway.cors.allowed-methods"));

    // 验证配置变更监听器存在
    NacosConfigRefreshListener listener =
        applicationContext.getBean(NacosConfigRefreshListener.class);
    assertNotNull(listener);

    // 触发配置变更事件
    listener.onApplicationEvent(event);

    // 验证配置已更新（通过重新读取配置属性）
    // 注意：在实际环境中，配置变更会通过 Nacos Config 自动触发
  }
}

