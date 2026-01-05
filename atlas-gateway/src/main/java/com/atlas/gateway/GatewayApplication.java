/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Gateway 应用主类
 *
 * <p>API 网关应用入口，提供路由转发、CORS 跨域支持、TraceId 链路追踪、统一错误返回、鉴权控制等功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@SpringBootApplication
public class GatewayApplication {

  /**
   * 应用主入口
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }
}

