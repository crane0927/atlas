/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * GatewayProperties 单元测试
 *
 * @author Atlas Team
 */
@SpringBootTest(classes = GatewayProperties.class)
@EnableConfigurationProperties(GatewayProperties.class)
@TestPropertySource(
    properties = {
      "atlas.gateway.routes[0].id=health-route",
      "atlas.gateway.routes[0].uri=http://localhost:8080",
      "atlas.gateway.routes[0].predicates[0]=Path=/health/**",
      "atlas.gateway.routes[0].filters[0]=StripPrefix=1",
      "atlas.gateway.whitelist.enabled=true",
      "atlas.gateway.whitelist.paths[0]=/health/**",
      "atlas.gateway.whitelist.paths[1]=/mock/**",
      "atlas.gateway.cors.allowed-origins=*",
      "atlas.gateway.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
      "atlas.gateway.cors.allowed-headers=*",
      "atlas.gateway.cors.allow-credentials=true",
      "atlas.gateway.cors.max-age=3600"
    })
class GatewayPropertiesTest {

  @Autowired private GatewayProperties gatewayProperties;

  @Test
  void testGatewayPropertiesCreation() {
    assertNotNull(gatewayProperties);
  }

  @Test
  void testRoutesConfiguration() {
    assertNotNull(gatewayProperties.getRoutes());
    assertEquals(1, gatewayProperties.getRoutes().size());

    GatewayProperties.RouteConfig route = gatewayProperties.getRoutes().get(0);
    assertEquals("health-route", route.getId());
    assertEquals("http://localhost:8080", route.getUri());
    assertEquals(1, route.getPredicates().size());
    assertEquals("Path=/health/**", route.getPredicates().get(0));
    assertEquals(1, route.getFilters().size());
    assertEquals("StripPrefix=1", route.getFilters().get(0));
  }

  @Test
  void testWhitelistConfiguration() {
    GatewayProperties.WhitelistConfig whitelist = gatewayProperties.getWhitelist();
    assertNotNull(whitelist);
    assertTrue(whitelist.getEnabled());
    assertEquals(2, whitelist.getPaths().size());
    assertTrue(whitelist.getPaths().contains("/health/**"));
    assertTrue(whitelist.getPaths().contains("/mock/**"));
  }

  @Test
  void testCorsConfiguration() {
    GatewayProperties.CorsConfig cors = gatewayProperties.getCors();
    assertNotNull(cors);
    assertEquals("*", cors.getAllowedOrigins());
    assertEquals("GET,POST,PUT,DELETE,OPTIONS", cors.getAllowedMethods());
    assertEquals("*", cors.getAllowedHeaders());
    assertTrue(cors.getAllowCredentials());
    assertEquals(3600, cors.getMaxAge());
  }

  @Test
  void testDefaultValues() {
    GatewayProperties properties = new GatewayProperties();
    assertNotNull(properties.getRoutes());
    assertNotNull(properties.getWhitelist());
    assertNotNull(properties.getCors());

    // 测试默认值
    assertTrue(properties.getWhitelist().getEnabled());
    assertEquals("*", properties.getCors().getAllowedOrigins());
    assertEquals("GET,POST,PUT,DELETE,OPTIONS", properties.getCors().getAllowedMethods());
    assertEquals("*", properties.getCors().getAllowedHeaders());
    assertTrue(properties.getCors().getAllowCredentials());
    assertEquals(3600, properties.getCors().getMaxAge());
  }

  @Test
  void testRouteConfigSettersAndGetters() {
    GatewayProperties.RouteConfig route = new GatewayProperties.RouteConfig();
    route.setId("test-route");
    route.setUri("http://localhost:9090");
    route.setPredicates(Arrays.asList("Path=/test/**"));
    route.setFilters(Arrays.asList("StripPrefix=1", "RewritePath=/test/(?<segment>.*), /$\\{segment}"));

    assertEquals("test-route", route.getId());
    assertEquals("http://localhost:9090", route.getUri());
    assertEquals(1, route.getPredicates().size());
    assertEquals(2, route.getFilters().size());
  }

  @Test
  void testWhitelistConfigSettersAndGetters() {
    GatewayProperties.WhitelistConfig whitelist = new GatewayProperties.WhitelistConfig();
    whitelist.setEnabled(false);
    whitelist.setPaths(Arrays.asList("/api/public/**", "/health/**"));

    assertTrue(!whitelist.getEnabled());
    assertEquals(2, whitelist.getPaths().size());
  }

  @Test
  void testCorsConfigSettersAndGetters() {
    GatewayProperties.CorsConfig cors = new GatewayProperties.CorsConfig();
    cors.setAllowedOrigins("http://localhost:3000,http://localhost:3001");
    cors.setAllowedMethods("GET,POST");
    cors.setAllowedHeaders("Content-Type,Authorization");
    cors.setAllowCredentials(false);
    cors.setMaxAge(1800);

    assertEquals("http://localhost:3000,http://localhost:3001", cors.getAllowedOrigins());
    assertEquals("GET,POST", cors.getAllowedMethods());
    assertEquals("Content-Type,Authorization", cors.getAllowedHeaders());
    assertTrue(!cors.getAllowCredentials());
    assertEquals(1800, cors.getMaxAge());
  }
}

