/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Atlas Auth 服务启动类
 *
 * <p>提供用户登录、登出、Token 签发与校验功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.atlas.system.api")
public class AuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthApplication.class, args);
  }
}

