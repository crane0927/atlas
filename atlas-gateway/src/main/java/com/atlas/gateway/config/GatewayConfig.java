/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.config;

import com.atlas.gateway.config.GatewayProperties.RouteConfig;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway 配置类
 *
 * <p>配置 Spring Cloud Gateway 路由规则，支持从 Nacos Config 读取路由配置并动态更新。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>从 {@link GatewayProperties} 读取路由配置
 *   <li>将路由配置转换为 {@link RouteDefinition}
 *   <li>支持路由规则动态更新（监听 Nacos Config 配置变更）
 *   <li>支持路径匹配和路径重写
 * </ul>
 *
 * <p>配置示例：
 *
 * <pre>{@code
 * atlas:
 *   gateway:
 *     routes:
 *       - id: health-route
 *         uri: http://localhost:8080
 *         predicates:
 *           - Path=/health/**
 *         filters:
 *           - StripPrefix=1
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GatewayConfig {

  private final GatewayProperties gatewayProperties;
  private RouteDefinitionWriter routeDefinitionWriter;

  public GatewayConfig(GatewayProperties gatewayProperties) {
    this.gatewayProperties = gatewayProperties;
  }

  @org.springframework.beans.factory.annotation.Autowired(required = false)
  public void setRouteDefinitionWriter(RouteDefinitionWriter routeDefinitionWriter) {
    this.routeDefinitionWriter = routeDefinitionWriter;
  }

  /**
   * 设置 RouteDefinitionWriter（用于测试）
   *
   * @param routeDefinitionWriter 路由定义写入器
   */
  public void setRouteDefinitionWriterForTest(RouteDefinitionWriter routeDefinitionWriter) {
    this.routeDefinitionWriter = routeDefinitionWriter;
  }

  /**
   * 创建路由定义定位器
   *
   * <p>从 {@link GatewayProperties} 读取路由配置，转换为 {@link RouteDefinition} 并返回。
   *
   * <p>路由配置支持通过 Nacos Config 动态更新，配置变更后会自动重新加载路由规则。
   *
   * @return 路由定义定位器
   */
  @Bean
  @Order(-1)
  public RouteDefinitionLocator routeDefinitionLocator() {
    return new RouteDefinitionLocator() {
      @Override
      public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        List<RouteConfig> routes = gatewayProperties.getRoutes();

        if (routes != null && !routes.isEmpty()) {
          for (RouteConfig routeConfig : routes) {
            try {
              RouteDefinition routeDefinition = convertToRouteDefinition(routeConfig);
              routeDefinitions.add(routeDefinition);
              log.debug("加载路由配置: id={}, uri={}", routeConfig.getId(), routeConfig.getUri());
            } catch (Exception e) {
              log.error("转换路由配置失败: id={}, error={}", routeConfig.getId(), e.getMessage(), e);
            }
          }
        }

        return Flux.fromIterable(routeDefinitions);
      }
    };
  }

  /**
   * 将 RouteConfig 转换为 RouteDefinition
   *
   * @param routeConfig 路由配置
   * @return 路由定义
   */
  private RouteDefinition convertToRouteDefinition(RouteConfig routeConfig) {
    RouteDefinition routeDefinition = new RouteDefinition();
    routeDefinition.setId(routeConfig.getId());
    routeDefinition.setUri(URI.create(routeConfig.getUri()));

    // 转换断言
    List<PredicateDefinition> predicates = new ArrayList<>();
    if (routeConfig.getPredicates() != null) {
      for (String predicate : routeConfig.getPredicates()) {
        PredicateDefinition predicateDefinition = new PredicateDefinition(predicate);
        predicates.add(predicateDefinition);
      }
    }
    routeDefinition.setPredicates(predicates);

    // 转换过滤器
    List<FilterDefinition> filters = new ArrayList<>();
    if (routeConfig.getFilters() != null) {
      for (String filter : routeConfig.getFilters()) {
        FilterDefinition filterDefinition = new FilterDefinition(filter);
        filters.add(filterDefinition);
      }
    }
    routeDefinition.setFilters(filters);

    return routeDefinition;
  }

  /**
   * 刷新路由配置
   *
   * <p>从 {@link GatewayProperties} 重新读取路由配置并更新到 Gateway。
   *
   * <p>此方法可以在 Nacos Config 配置变更时调用，实现路由规则动态更新。
   *
   * <p>注意：如果 RouteDefinitionWriter 不可用（例如在测试环境中），此方法不会执行任何操作。
   */
  public void refreshRoutes() {
    if (routeDefinitionWriter == null) {
      log.warn("RouteDefinitionWriter 不可用，跳过路由配置刷新");
      return;
    }

    log.info("开始刷新路由配置...");
    List<RouteConfig> routes = gatewayProperties.getRoutes();

    if (routes != null && !routes.isEmpty()) {
      for (RouteConfig routeConfig : routes) {
        try {
          RouteDefinition routeDefinition = convertToRouteDefinition(routeConfig);
          // 删除旧路由
          routeDefinitionWriter.delete(Mono.just(routeDefinition.getId())).subscribe();
          // 添加新路由
          routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
          log.info("刷新路由配置成功: id={}, uri={}", routeConfig.getId(), routeConfig.getUri());
        } catch (Exception e) {
          log.error(
              "刷新路由配置失败: id={}, error={}", routeConfig.getId(), e.getMessage(), e);
        }
      }
    }
    log.info("路由配置刷新完成");
  }
}

