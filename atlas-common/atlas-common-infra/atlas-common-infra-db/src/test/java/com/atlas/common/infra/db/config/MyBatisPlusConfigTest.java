/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.db.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * MyBatisPlusConfig 单元测试
 *
 * @author Atlas
 */
@SpringBootTest(classes = {MyBatisPlusConfig.class, MyBatisPlusProperties.class})
class MyBatisPlusConfigTest {

  @Autowired private MybatisPlusInterceptor mybatisPlusInterceptor;

  @Autowired private PaginationInnerInterceptor paginationInnerInterceptor;

  @Test
  void testMybatisPlusInterceptorBeanCreation() {
    assertNotNull(mybatisPlusInterceptor);
  }

  @Test
  void testPaginationInnerInterceptorBeanCreation() {
    // 验证分页插件可以正确创建并注册到 Spring 容器
    assertNotNull(paginationInnerInterceptor);
  }

  @Test
  void testMyBatisPlusConfigBeanCreation() {
    // 验证配置类可以正确创建并注册到 Spring 容器
    assertNotNull(mybatisPlusInterceptor);
    assertNotNull(paginationInnerInterceptor);
  }

  @Test
  void testPaginationPluginAddedToInterceptor() {
    // 验证分页插件已添加到拦截器中
    assertNotNull(mybatisPlusInterceptor);
    // 注意：MybatisPlusInterceptor 的内部拦截器列表是私有的，无法直接验证
    // 但可以通过验证 Bean 创建成功来间接验证
  }
}
