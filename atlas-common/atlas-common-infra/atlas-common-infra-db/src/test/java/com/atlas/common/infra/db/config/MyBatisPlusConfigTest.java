/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.db.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
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

  @Test
  void testMybatisPlusInterceptorBeanCreation() {
    assertNotNull(mybatisPlusInterceptor);
  }

  @Test
  void testMyBatisPlusConfigBeanCreation() {
    // 验证配置类可以正确创建并注册到 Spring 容器
    assertNotNull(mybatisPlusInterceptor);
  }
}

