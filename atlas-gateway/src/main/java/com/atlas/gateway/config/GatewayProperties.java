/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Gateway 配置属性类
 *
 * <p>用于读取配置文件中的 Gateway 相关配置，支持通过 application.yml 或 Nacos Config 配置路由规则、白名单、CORS 等参数。
 *
 * <p>配置特性：
 *
 * <ul>
 *   <li>支持从 Nacos Config 读取配置
 *   <li>支持配置动态更新（通过 {@code @ConfigurationProperties} 自动绑定）
 *   <li>配置项符合项目的配置命名规范（{@code atlas.gateway.*}）
 *   <li>所有配置项都支持动态更新，无需重启服务
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
 *     whitelist:
 *       enabled: true
 *       paths:
 *         - /health/**
 *         - /mock/**
 *     cors:
 *       allowed-origins: "*"
 *       allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
 *       allowed-headers: "*"
 *       allow-credentials: true
 *       max-age: 3600
 * }</pre>
 *
 * <p>配置命名规范：
 *
 * <ul>
 *   <li>所有配置项使用 {@code atlas.gateway.*} 前缀
 *   <li>路由配置：{@code atlas.gateway.routes.*}
 *   <li>白名单配置：{@code atlas.gateway.whitelist.*}
 *   <li>CORS 配置：{@code atlas.gateway.cors.*}
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "atlas.gateway")
public class GatewayProperties {

  /** 路由规则列表 */
  private List<RouteConfig> routes = new ArrayList<>();

  /** 白名单配置 */
  private WhitelistConfig whitelist = new WhitelistConfig();

  /** CORS 配置 */
  private CorsConfig cors = new CorsConfig();

  /**
   * 路由配置类
   *
   * <p>定义请求路径与后端服务的映射关系，包括路由 ID、URI、断言和过滤器等。
   *
   * @author Atlas Team
   * @since 1.0.0
   */
  @Data
  public static class RouteConfig {

    /** 路由 ID，唯一标识 */
    private String id;

    /** 后端服务 URI */
    private String uri;

    /** 路由断言（匹配条件），如 Path=/health/** */
    private List<String> predicates = new ArrayList<>();

    /** 路由过滤器（路径重写等），如 StripPrefix=1 */
    private List<String> filters = new ArrayList<>();
  }

  /**
   * 白名单配置类
   *
   * <p>定义无需鉴权的路径列表，支持通配符匹配。
   *
   * @author Atlas Team
   * @since 1.0.0
   */
  @Data
  public static class WhitelistConfig {

    /** 是否启用白名单，默认值为 true */
    private Boolean enabled = true;

    /** 白名单路径列表（支持通配符），如 /health/**、/api/public/** */
    private List<String> paths = new ArrayList<>();
  }

  /**
   * CORS 配置类
   *
   * <p>定义 Gateway CORS 跨域配置，包括允许的源、方法、请求头等。
   *
   * @author Atlas Team
   * @since 1.0.0
   */
  @Data
  public static class CorsConfig {

    /** 允许的源（多个用逗号分隔，* 表示所有），默认值为 "*" */
    private String allowedOrigins = "*";

    /** 允许的 HTTP 方法（多个用逗号分隔），默认值为 "GET,POST,PUT,DELETE,OPTIONS" */
    private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";

    /** 允许的请求头（多个用逗号分隔，* 表示所有），默认值为 "*" */
    private String allowedHeaders = "*";

    /** 是否允许携带凭证，默认值为 true */
    private Boolean allowCredentials = true;

    /** 预检请求缓存时间（秒），默认值为 3600 */
    private Integer maxAge = 3600;
  }
}

