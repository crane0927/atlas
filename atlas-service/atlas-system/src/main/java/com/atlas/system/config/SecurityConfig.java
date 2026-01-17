/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全配置类
 *
 * <p>提供密码加密器 Bean 配置。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Configuration
public class SecurityConfig {

  /**
   * 配置密码加密器
   *
   * <p>使用 BCrypt 算法进行密码加密，强度参数为 10（默认）。
   *
   * @return PasswordEncoder 实例
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
