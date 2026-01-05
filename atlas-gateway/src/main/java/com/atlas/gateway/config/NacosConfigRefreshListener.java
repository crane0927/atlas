/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Nacos Config 配置变更监听器
 *
 * <p>监听 Nacos Config 配置变更事件，当 Gateway 配置变更时自动刷新路由规则和 CORS 配置。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>监听 Spring Cloud 的 EnvironmentChangeEvent 事件（Nacos Config 配置变更时会触发）
 *   <li>配置变更后自动调用 {@link GatewayConfig#refreshRoutes()} 刷新路由规则
 *   <li>配置变更后自动调用 {@link CorsConfig#refreshCorsConfig()} 刷新 CORS 配置
 *   <li>支持路由规则和 CORS 配置动态更新，无需重启服务
 * </ul>
 *
 * <p>注意：此监听器依赖于 Spring Cloud 的配置刷新机制。当 Nacos Config 配置变更时，
 * Spring Cloud 会自动刷新配置并发布 EnvironmentChangeEvent 事件。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class NacosConfigRefreshListener implements ApplicationListener<EnvironmentChangeEvent> {

  private final GatewayConfig gatewayConfig;
  private final CorsConfig corsConfig;

  @Autowired
  public NacosConfigRefreshListener(GatewayConfig gatewayConfig, CorsConfig corsConfig) {
    this.gatewayConfig = gatewayConfig;
    this.corsConfig = corsConfig;
  }

  /**
   * 处理环境变更事件
   *
   * <p>当 Nacos Config 配置变更时，Spring Cloud 会发布 EnvironmentChangeEvent 事件。
   * 如果变更的配置项包含 Gateway 相关配置（atlas.gateway.*），则刷新路由规则和 CORS 配置。
   *
   * @param event 环境变更事件
   */
  @Override
  public void onApplicationEvent(EnvironmentChangeEvent event) {
    // 检查是否有 Gateway 相关配置变更
    boolean hasGatewayConfigChange =
        event.getKeys().stream().anyMatch(key -> key.startsWith("atlas.gateway"));

    if (hasGatewayConfigChange) {
      log.info("检测到 Gateway 配置变更，开始刷新配置...");

      // 检查是否有路由配置变更
      boolean hasRouteConfigChange =
          event.getKeys().stream().anyMatch(key -> key.startsWith("atlas.gateway.routes"));
      if (hasRouteConfigChange) {
        try {
          gatewayConfig.refreshRoutes();
          log.info("路由规则刷新成功");
        } catch (Exception e) {
          log.error("路由规则刷新失败", e);
        }
      }

      // 检查是否有 CORS 配置变更
      boolean hasCorsConfigChange =
          event.getKeys().stream().anyMatch(key -> key.startsWith("atlas.gateway.cors"));
      if (hasCorsConfigChange) {
        try {
          corsConfig.refreshCorsConfig();
          log.info("CORS 配置刷新成功");
        } catch (Exception e) {
          log.error("CORS 配置刷新失败", e);
        }
      }

      // 检查是否有白名单配置变更
      boolean hasWhitelistConfigChange =
          event.getKeys().stream().anyMatch(key -> key.startsWith("atlas.gateway.whitelist"));
      if (hasWhitelistConfigChange) {
        log.info("检测到白名单配置变更，白名单配置已自动刷新（通过 GatewayProperties 自动绑定）");
      }
    }
  }
}

