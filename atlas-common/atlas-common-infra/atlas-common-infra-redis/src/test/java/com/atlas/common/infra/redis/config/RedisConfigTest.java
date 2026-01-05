/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.redis.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfig 单元测试
 *
 * @author Atlas
 */
@SpringBootTest(
    classes = {RedisConfig.class, RedisProperties.class},
    properties = {"spring.data.redis.host=localhost", "spring.data.redis.port=6379"})
class RedisConfigTest {

  @Autowired private RedisTemplate<String, Object> redisTemplate;

  @Autowired private RedisConnectionFactory redisConnectionFactory;

  @Test
  void testRedisTemplateBeanCreation() {
    assertNotNull(redisTemplate);
  }

  @Test
  void testKeySerializer() {
    RedisSerializer<?> keySerializer = redisTemplate.getKeySerializer();
    assertNotNull(keySerializer);
    assertTrue(keySerializer instanceof StringRedisSerializer);
  }

  @Test
  void testValueSerializer() {
    RedisSerializer<?> valueSerializer = redisTemplate.getValueSerializer();
    assertNotNull(valueSerializer);
    assertTrue(valueSerializer instanceof GenericJackson2JsonRedisSerializer);
  }

  @Test
  void testHashKeySerializer() {
    RedisSerializer<?> hashKeySerializer = redisTemplate.getHashKeySerializer();
    assertNotNull(hashKeySerializer);
    assertTrue(hashKeySerializer instanceof StringRedisSerializer);
  }

  @Test
  void testHashValueSerializer() {
    RedisSerializer<?> hashValueSerializer = redisTemplate.getHashValueSerializer();
    assertNotNull(hashValueSerializer);
    assertTrue(hashValueSerializer instanceof GenericJackson2JsonRedisSerializer);
  }

  @Test
  void testConnectionFactory() {
    assertNotNull(redisConnectionFactory);
    assertNotNull(redisTemplate.getConnectionFactory());
  }
}

