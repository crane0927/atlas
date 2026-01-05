/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.db.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
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
 *
 * <ul>
 *   <li>通过 {@link MyBatisPlusProperties} 读取配置文件中的参数
 *   <li>配置 {@link MybatisPlusInterceptor} 拦截器，用于添加插件
 *   <li>配置 {@link PaginationInnerInterceptor} 分页插件，支持 PostgreSQL 数据库
 * </ul>
 *
 * <p>分页插件配置：
 *
 * <ul>
 *   <li>数据库类型：从配置文件中读取，默认为 PostgreSQL
 *   <li>最大每页数量：从配置文件中读取，默认为 1000
 *   <li>溢出处理：从配置文件中读取，默认为 false
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
   * 创建分页插件 Bean
   *
   * <p>配置分页插件，支持自定义分页参数（最大每页数量、溢出处理、数据库类型等）。
   *
   * <p>配置参数从 {@link MyBatisPlusProperties} 中读取，支持通过 application.yml 自定义配置。
   *
   * @return 分页插件实例
   */
  @Bean
  public PaginationInnerInterceptor paginationInnerInterceptor() {
    MyBatisPlusProperties.PaginationProperties pagination = myBatisPlusProperties.getPagination();
    PaginationInnerInterceptor paginationInterceptor =
        new PaginationInnerInterceptor(pagination.getDbType());
    paginationInterceptor.setMaxLimit(pagination.getMaxLimit());
    paginationInterceptor.setOverflow(pagination.getOverflow());
    return paginationInterceptor;
  }

  /**
   * 创建 MyBatis-Plus 拦截器 Bean
   *
   * <p>配置 MyBatis-Plus 拦截器，用于添加各种插件（如分页插件）。
   *
   * <p>当前已添加分页插件，支持自动拦截带有 {@link com.baomidou.mybatisplus.extension.plugins.pagination.Page}
   * 参数的方法，自动添加分页 SQL。
   *
   * @return MyBatis-Plus 拦截器实例
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 添加分页插件
    interceptor.addInnerInterceptor(paginationInnerInterceptor());
    return interceptor;
  }
}
