/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.redis.key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.atlas.common.infra.redis.config.RedisProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * RedisKeyBuilder 单元测试
 *
 * @author Atlas
 */
class RedisKeyBuilderTest {

  @BeforeEach
  void setUp() {
    // 重置 Key 前缀为默认值
    RedisKeyBuilder.init(new RedisProperties());
  }

  @Test
  void testBuilderCreation() {
    RedisKeyBuilder builder = RedisKeyBuilder.builder();
    assertNotNull(builder);
  }

  @Test
  void testBuildKeyWithDefaultPrefix() {
    String key = RedisKeyBuilder.builder().module("user").business("info").id("123").build();

    assertEquals("atlas:user:info:123", key);
  }

  @Test
  void testBuildKeyWithCustomPrefix() {
    // 设置自定义前缀
    RedisProperties properties = new RedisProperties();
    properties.setKeyPrefix("custom");
    RedisKeyBuilder.init(properties);

    String key = RedisKeyBuilder.builder().module("user").business("info").id("123").build();

    assertEquals("custom:user:info:123", key);
  }

  @Test
  void testModuleMethod() {
    RedisKeyBuilder builder = RedisKeyBuilder.builder().module("order");
    assertNotNull(builder);
  }

  @Test
  void testBusinessMethod() {
    RedisKeyBuilder builder = RedisKeyBuilder.builder().business("detail");
    assertNotNull(builder);
  }

  @Test
  void testIdMethod() {
    RedisKeyBuilder builder = RedisKeyBuilder.builder().id("456");
    assertNotNull(builder);
  }

  @Test
  void testWithTtlMethod() {
    RedisKeyBuilder builder =
        RedisKeyBuilder.builder().module("user").business("info").id("123").withTtl(3600);

    assertNotNull(builder);
    assertEquals(3600, builder.getTtl());
  }

  @Test
  void testChainMethods() {
    String key =
        RedisKeyBuilder.builder().module("user").business("info").id("123").withTtl(3600).build();

    assertEquals("atlas:user:info:123", key);
  }

  @Test
  void testBuildWithoutModule() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RedisKeyBuilder.builder().business("info").id("123").build());
  }

  @Test
  void testBuildWithoutBusiness() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RedisKeyBuilder.builder().module("user").id("123").build());
  }

  @Test
  void testBuildWithoutId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RedisKeyBuilder.builder().module("user").business("info").build());
  }

  @Test
  void testBuildWithEmptyModule() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RedisKeyBuilder.builder().module("").business("info").id("123").build());
  }

  @Test
  void testBuildWithEmptyBusiness() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RedisKeyBuilder.builder().module("user").business("").id("123").build());
  }

  @Test
  void testBuildWithEmptyId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RedisKeyBuilder.builder().module("user").business("info").id("").build());
  }

  @Test
  void testBuildWithWhitespaceModule() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RedisKeyBuilder.builder().module(" ").business("info").id("123").build());
  }

  @Test
  void testBuildWithWhitespaceBusiness() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RedisKeyBuilder.builder().module("user").business(" ").id("123").build());
  }

  @Test
  void testBuildWithWhitespaceId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RedisKeyBuilder.builder().module("user").business("info").id(" ").build());
  }

  @Test
  void testGetTtlWhenNotSet() {
    RedisKeyBuilder builder = RedisKeyBuilder.builder().module("user").business("info").id("123");

    assertNull(builder.getTtl());
  }

  @Test
  void testComplexKey() {
    String key =
        RedisKeyBuilder.builder()
            .module("order")
            .business("payment")
            .id("order-12345-payment-67890")
            .build();

    assertEquals("atlas:order:payment:order-12345-payment-67890", key);
  }
}
