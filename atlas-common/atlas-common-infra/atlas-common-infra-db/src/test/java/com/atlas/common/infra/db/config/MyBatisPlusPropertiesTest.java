/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.db.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.baomidou.mybatisplus.annotation.DbType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * MyBatisPlusProperties 单元测试
 *
 * @author Atlas
 */
@SpringBootTest(classes = MyBatisPlusProperties.class)
@EnableConfigurationProperties(MyBatisPlusProperties.class)
@TestPropertySource(
    properties = {
      "atlas.mybatis-plus.pagination.max-limit=2000",
      "atlas.mybatis-plus.pagination.overflow=true",
      "atlas.mybatis-plus.pagination.db-type=MYSQL"
    })
class MyBatisPlusPropertiesTest {

  @Autowired private MyBatisPlusProperties myBatisPlusProperties;

  @Test
  void testMyBatisPlusPropertiesBeanCreation() {
    assertNotNull(myBatisPlusProperties);
  }

  @Test
  void testPaginationPropertiesFromConfiguration() {
    assertNotNull(myBatisPlusProperties.getPagination());
    assertEquals(2000L, myBatisPlusProperties.getPagination().getMaxLimit());
    assertEquals(true, myBatisPlusProperties.getPagination().getOverflow());
    assertEquals(DbType.MYSQL, myBatisPlusProperties.getPagination().getDbType());
  }

  @Test
  void testDefaultPaginationProperties() {
    MyBatisPlusProperties properties = new MyBatisPlusProperties();
    assertNotNull(properties.getPagination());
    assertEquals(1000L, properties.getPagination().getMaxLimit());
    assertEquals(false, properties.getPagination().getOverflow());
    assertEquals(DbType.POSTGRE_SQL, properties.getPagination().getDbType());
  }

  @Test
  void testSetPaginationProperties() {
    MyBatisPlusProperties properties = new MyBatisPlusProperties();
    MyBatisPlusProperties.PaginationProperties pagination =
        new MyBatisPlusProperties.PaginationProperties();
    pagination.setMaxLimit(3000L);
    pagination.setOverflow(true);
    pagination.setDbType(DbType.ORACLE);
    properties.setPagination(pagination);

    assertEquals(3000L, properties.getPagination().getMaxLimit());
    assertEquals(true, properties.getPagination().getOverflow());
    assertEquals(DbType.ORACLE, properties.getPagination().getDbType());
  }
}
