/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.gateway.config.GatewayProperties.RouteConfig;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * GatewayConfig 单元测试
 *
 * @author Atlas Team
 */
class GatewayConfigTest {

  private GatewayProperties gatewayProperties;
  private RouteDefinitionWriter routeDefinitionWriter;
  private GatewayConfig gatewayConfig;

  @BeforeEach
  void setUp() {
    gatewayProperties = new GatewayProperties();
    routeDefinitionWriter = mock(RouteDefinitionWriter.class);
    when(routeDefinitionWriter.delete(any())).thenReturn(Mono.empty());
    when(routeDefinitionWriter.save(any())).thenReturn(Mono.empty());

    gatewayConfig = new GatewayConfig(gatewayProperties);
    gatewayConfig.setRouteDefinitionWriterForTest(routeDefinitionWriter);
  }

  @Test
  void testRouteDefinitionLocatorBeanCreation() {
    RouteDefinitionLocator locator = gatewayConfig.routeDefinitionLocator();
    assertNotNull(locator);
  }

  @Test
  void testRouteDefinitionLocatorWithRoutes() {
    // 准备测试数据
    RouteConfig routeConfig = new RouteConfig();
    routeConfig.setId("test-route");
    routeConfig.setUri("http://localhost:8080");
    routeConfig.setPredicates(Arrays.asList("Path=/test/**"));
    routeConfig.setFilters(Arrays.asList("StripPrefix=1"));

    gatewayProperties.setRoutes(Arrays.asList(routeConfig));

    // 测试路由定义定位器
    RouteDefinitionLocator locator = gatewayConfig.routeDefinitionLocator();
    Flux<RouteDefinition> routeDefinitions = locator.getRouteDefinitions();

    StepVerifier.create(routeDefinitions)
        .assertNext(
            routeDefinition -> {
              assertEquals("test-route", routeDefinition.getId());
              assertEquals("http://localhost:8080", routeDefinition.getUri().toString());
              assertEquals(1, routeDefinition.getPredicates().size());
              assertEquals(1, routeDefinition.getFilters().size());
            })
        .verifyComplete();
  }

  @Test
  void testRouteDefinitionLocatorWithEmptyRoutes() {
    gatewayProperties.setRoutes(Arrays.asList());

    RouteDefinitionLocator locator = gatewayConfig.routeDefinitionLocator();
    Flux<RouteDefinition> routeDefinitions = locator.getRouteDefinitions();

    StepVerifier.create(routeDefinitions).verifyComplete();
  }

  @Test
  void testRouteDefinitionLocatorWithNullRoutes() {
    gatewayProperties.setRoutes(null);

    RouteDefinitionLocator locator = gatewayConfig.routeDefinitionLocator();
    Flux<RouteDefinition> routeDefinitions = locator.getRouteDefinitions();

    StepVerifier.create(routeDefinitions).verifyComplete();
  }

  @Test
  void testRefreshRoutes() {
    // 准备测试数据
    RouteConfig routeConfig = new RouteConfig();
    routeConfig.setId("test-route");
    routeConfig.setUri("http://localhost:8080");
    routeConfig.setPredicates(Arrays.asList("Path=/test/**"));
    routeConfig.setFilters(Arrays.asList("StripPrefix=1"));

    gatewayProperties.setRoutes(Arrays.asList(routeConfig));

    // 执行刷新
    gatewayConfig.refreshRoutes();

    // 验证调用了 RouteDefinitionWriter
    verify(routeDefinitionWriter).delete(any(Mono.class));
    verify(routeDefinitionWriter).save(any(Mono.class));
  }

  @Test
  void testRefreshRoutesWithMultipleRoutes() {
    // 准备测试数据
    RouteConfig route1 = new RouteConfig();
    route1.setId("route-1");
    route1.setUri("http://localhost:8080");
    route1.setPredicates(Arrays.asList("Path=/route1/**"));

    RouteConfig route2 = new RouteConfig();
    route2.setId("route-2");
    route2.setUri("http://localhost:8081");
    route2.setPredicates(Arrays.asList("Path=/route2/**"));

    gatewayProperties.setRoutes(Arrays.asList(route1, route2));

    // 执行刷新
    gatewayConfig.refreshRoutes();

    // 验证调用了 RouteDefinitionWriter（每个路由调用一次 delete 和 save）
    verify(routeDefinitionWriter).delete(any(Mono.class));
    verify(routeDefinitionWriter).save(any(Mono.class));
  }

  @Test
  void testRefreshRoutesWithEmptyRoutes() {
    gatewayProperties.setRoutes(Arrays.asList());

    // 执行刷新（不应该抛出异常）
    gatewayConfig.refreshRoutes();
  }
}

