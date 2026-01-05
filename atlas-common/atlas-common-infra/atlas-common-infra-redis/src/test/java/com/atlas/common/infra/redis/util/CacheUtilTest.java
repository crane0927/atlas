/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.redis.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * CacheUtil 单元测试
 *
 * @author Atlas
 */
class CacheUtilTest {

  private RedisTemplate<String, Object> redisTemplate;
  private ValueOperations<String, Object> valueOperations;

  @BeforeEach
  void setUp() throws Exception {
    redisTemplate = mock(RedisTemplate.class);
    valueOperations = mock(ValueOperations.class);

    // 使用反射设置静态字段
    java.lang.reflect.Field field = CacheUtil.class.getDeclaredField("redisTemplate");
    field.setAccessible(true);
    field.set(null, redisTemplate);

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  void testSet() {
    // When
    CacheUtil.set("test:key", "testValue");

    // Then
    verify(valueOperations).set("test:key", "testValue");
  }

  @Test
  void testSetWithExpire() {
    // When
    CacheUtil.set("test:key", "testValue", 3600);

    // Then
    verify(valueOperations).set(eq("test:key"), eq("testValue"), eq(Duration.ofSeconds(3600)));
  }

  @Test
  void testSetExceptionHandling() {
    // Given
    doThrow(new RuntimeException("Redis error")).when(valueOperations).set(anyString(), any());

    // When
    CacheUtil.set("test:key", "testValue");

    // Then - 不应该抛出异常，只记录日志
    verify(valueOperations).set("test:key", "testValue");
  }

  @Test
  void testGet() {
    // Given
    when(valueOperations.get("test:key")).thenReturn("testValue");

    // When
    String result = CacheUtil.get("test:key", String.class);

    // Then
    assertNotNull(result);
    assertEquals("testValue", result);
    verify(valueOperations).get("test:key");
  }

  @Test
  void testGetNull() {
    // Given
    when(valueOperations.get("test:key")).thenReturn(null);

    // When
    String result = CacheUtil.get("test:key", String.class);

    // Then
    assertNull(result);
    verify(valueOperations).get("test:key");
  }

  @Test
  void testGetExceptionHandling() {
    // Given
    when(valueOperations.get(anyString())).thenThrow(new RuntimeException("Redis error"));

    // When
    String result = CacheUtil.get("test:key", String.class);

    // Then
    assertNull(result);
    verify(valueOperations).get("test:key");
  }

  @Test
  void testDelete() {
    // When
    CacheUtil.delete("test:key");

    // Then
    verify(redisTemplate).delete("test:key");
  }

  @Test
  void testDeleteExceptionHandling() {
    // Given
    doThrow(new RuntimeException("Redis error")).when(redisTemplate).delete(anyString());

    // When
    CacheUtil.delete("test:key");

    // Then - 不应该抛出异常
    verify(redisTemplate).delete("test:key");
  }

  @Test
  void testDeletePattern() {
    // Given
    Set<String> keys = Set.of("test:key1", "test:key2");
    when(redisTemplate.keys("test:*")).thenReturn(keys);

    // When
    CacheUtil.deletePattern("test:*");

    // Then
    verify(redisTemplate).keys("test:*");
    verify(redisTemplate).delete(keys);
  }

  @Test
  void testDeletePatternEmpty() {
    // Given
    when(redisTemplate.keys("test:*")).thenReturn(Collections.emptySet());

    // When
    CacheUtil.deletePattern("test:*");

    // Then
    verify(redisTemplate).keys("test:*");
    verify(redisTemplate, never()).delete(any());
  }

  @Test
  void testDeletePatternNull() {
    // Given
    when(redisTemplate.keys("test:*")).thenReturn(null);

    // When
    CacheUtil.deletePattern("test:*");

    // Then
    verify(redisTemplate).keys("test:*");
    verify(redisTemplate, never()).delete(any());
  }

  @Test
  void testDeletePatternExceptionHandling() {
    // Given
    when(redisTemplate.keys(anyString())).thenThrow(new RuntimeException("Redis error"));

    // When
    CacheUtil.deletePattern("test:*");

    // Then - 不应该抛出异常
    verify(redisTemplate).keys("test:*");
  }

  @Test
  void testExists() {
    // Given
    when(redisTemplate.hasKey("test:key")).thenReturn(true);

    // When
    boolean result = CacheUtil.exists("test:key");

    // Then
    assertTrue(result);
    verify(redisTemplate).hasKey("test:key");
  }

  @Test
  void testExistsFalse() {
    // Given
    when(redisTemplate.hasKey("test:key")).thenReturn(false);

    // When
    boolean result = CacheUtil.exists("test:key");

    // Then
    assertFalse(result);
    verify(redisTemplate).hasKey("test:key");
  }

  @Test
  void testExistsNull() {
    // Given
    when(redisTemplate.hasKey("test:key")).thenReturn(null);

    // When
    boolean result = CacheUtil.exists("test:key");

    // Then
    assertFalse(result);
    verify(redisTemplate).hasKey("test:key");
  }

  @Test
  void testExistsExceptionHandling() {
    // Given
    when(redisTemplate.hasKey(anyString())).thenThrow(new RuntimeException("Redis error"));

    // When
    boolean result = CacheUtil.exists("test:key");

    // Then
    assertFalse(result);
    verify(redisTemplate).hasKey("test:key");
  }

  @Test
  void testExpire() {
    // When
    CacheUtil.expire("test:key", 3600);

    // Then
    verify(redisTemplate).expire(eq("test:key"), eq(Duration.ofSeconds(3600)));
  }

  @Test
  void testExpireExceptionHandling() {
    // Given
    doThrow(new RuntimeException("Redis error")).when(redisTemplate).expire(anyString(), any());

    // When
    CacheUtil.expire("test:key", 3600);

    // Then - 不应该抛出异常
    verify(redisTemplate).expire(eq("test:key"), eq(Duration.ofSeconds(3600)));
  }

  @Test
  void testGetExpire() {
    // Given
    when(redisTemplate.getExpire("test:key")).thenReturn(3600L);

    // When
    long result = CacheUtil.getExpire("test:key");

    // Then
    assertEquals(3600L, result);
    verify(redisTemplate).getExpire("test:key");
  }

  @Test
  void testGetExpireNull() {
    // Given
    when(redisTemplate.getExpire("test:key")).thenReturn(null);

    // When
    long result = CacheUtil.getExpire("test:key");

    // Then
    assertEquals(-1L, result);
    verify(redisTemplate).getExpire("test:key");
  }

  @Test
  void testGetExpireExceptionHandling() {
    // Given
    when(redisTemplate.getExpire(anyString())).thenThrow(new RuntimeException("Redis error"));

    // When
    long result = CacheUtil.getExpire("test:key");

    // Then
    assertEquals(-1L, result);
    verify(redisTemplate).getExpire("test:key");
  }
}
