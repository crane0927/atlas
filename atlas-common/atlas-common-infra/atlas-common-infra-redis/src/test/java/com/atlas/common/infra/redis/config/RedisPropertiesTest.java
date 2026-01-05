/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.redis.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * RedisProperties 单元测试
 *
 * @author Atlas
 */
@SpringBootTest(classes = RedisProperties.class)
@EnableConfigurationProperties(RedisProperties.class)
@TestPropertySource(properties = {"atlas.redis.key-prefix=test-prefix"})
class RedisPropertiesTest {

  @Autowired private RedisProperties redisProperties;

  @Test
  void testRedisPropertiesBeanCreation() {
    assertNotNull(redisProperties);
  }

  @Test
  void testKeyPrefixFromConfiguration() {
    assertEquals("test-prefix", redisProperties.getKeyPrefix());
  }

  @Test
  void testDefaultKeyPrefix() {
    RedisProperties properties = new RedisProperties();
    assertEquals("atlas", properties.getKeyPrefix());
  }

  @Test
  void testSetKeyPrefix() {
    RedisProperties properties = new RedisProperties();
    properties.setKeyPrefix("custom-prefix");
    assertEquals("custom-prefix", properties.getKeyPrefix());
  }
}
