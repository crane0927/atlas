/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.db.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 *
 * <p>提供统一的 MyBatis-Plus 配置，包括拦截器、分页插件等。
 *
 * <p>该配置类会自动注册到 Spring 容器，业务模块引入该模块后即可使用 MyBatis-Plus 功能。
 *
 * <p>配置说明：
 * <ul>
 *   <li>通过 {@link MyBatisPlusProperties} 读取配置文件中的参数</li>
 *   <li>配置 {@link MybatisPlusInterceptor} 拦截器，用于添加插件</li>
 *   <li>分页插件配置在 Phase 3 中实现</li>
 * </ul>
 *
 * @author Atlas
 */
@Configuration
@EnableConfigurationProperties(MyBatisPlusProperties.class)
public class MyBatisPlusConfig {

  private final MyBatisPlusProperties myBatisPlusProperties;

  public MyBatisPlusConfig(MyBatisPlusProperties myBatisPlusProperties) {
    this.myBatisPlusProperties = myBatisPlusProperties;
  }

  /**
   * 创建 MyBatis-Plus 拦截器 Bean
   *
   * <p>配置 MyBatis-Plus 拦截器，用于添加各种插件（如分页插件）。
   *
   * <p>注意：分页插件将在 Phase 3 中添加。
   *
   * @return MyBatis-Plus 拦截器实例
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 分页插件将在 Phase 3 中添加
    return interceptor;
  }
}

