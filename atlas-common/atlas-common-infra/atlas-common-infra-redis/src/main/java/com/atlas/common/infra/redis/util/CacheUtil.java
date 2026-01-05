/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.redis.util;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

/**
 * 缓存工具类
 *
 * <p>提供常用的缓存操作方法封装，统一处理异常和日志记录。
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // 设置缓存
 * CacheUtil.set("atlas:user:info:123", userInfo);
 *
 * // 设置缓存并指定过期时间
 * CacheUtil.set("atlas:user:info:123", userInfo, 3600);
 *
 * // 获取缓存
 * UserInfo userInfo = CacheUtil.get("atlas:user:info:123", UserInfo.class);
 *
 * // 删除缓存
 * CacheUtil.delete("atlas:user:info:123");
 *
 * // 检查缓存是否存在
 * boolean exists = CacheUtil.exists("atlas:user:info:123");
 *
 * // 设置过期时间
 * CacheUtil.expire("atlas:user:info:123", 3600);
 *
 * // 获取剩余过期时间
 * long ttl = CacheUtil.getExpire("atlas:user:info:123");
 * }</pre>
 *
 * @author Atlas
 */
@Slf4j
@Component
public class CacheUtil {

  private static RedisTemplate<String, Object> redisTemplate;

  /**
   * 注入 RedisTemplate（由 Spring 自动调用）
   *
   * @param redisTemplate RedisTemplate 实例
   */
  @Autowired
  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    CacheUtil.redisTemplate = redisTemplate;
  }

  /**
   * 设置缓存
   *
   * @param key 缓存 Key
   * @param value 缓存值
   */
  public static void set(String key, Object value) {
    try {
      redisTemplate.opsForValue().set(key, value);
    } catch (Exception e) {
      log.error("缓存设置失败: key={}", key, e);
    }
  }

  /**
   * 设置缓存并指定过期时间
   *
   * @param key 缓存 Key
   * @param value 缓存值
   * @param seconds 过期时间（秒）
   */
  public static void set(String key, Object value, int seconds) {
    try {
      redisTemplate.opsForValue().set(key, value, java.time.Duration.ofSeconds(seconds));
    } catch (Exception e) {
      log.error("缓存设置失败: key={}, seconds={}", key, seconds, e);
    }
  }

  /**
   * 获取缓存
   *
   * @param key 缓存 Key
   * @param clazz 返回值类型
   * @param <T> 泛型类型
   * @return 缓存值，如果 Key 不存在则返回 null
   */
  public static <T> T get(String key, Class<T> clazz) {
    try {
      Object value = redisTemplate.opsForValue().get(key);
      if (value == null) {
        return null;
      }
      return clazz.cast(value);
    } catch (Exception e) {
      log.error("缓存获取失败: key={}", key, e);
      return null;
    }
  }

  /**
   * 删除缓存
   *
   * @param key 缓存 Key
   */
  public static void delete(String key) {
    try {
      redisTemplate.delete(key);
    } catch (Exception e) {
      log.error("缓存删除失败: key={}", key, e);
    }
  }

  /**
   * 按模式删除缓存
   *
   * <p>使用 SCAN 命令遍历匹配的 Key，避免阻塞 Redis。
   *
   * @param pattern 匹配模式（如：atlas:user:*）
   */
  public static void deletePattern(String pattern) {
    try {
      Set<String> keys = redisTemplate.keys(pattern);
      if (keys != null && !keys.isEmpty()) {
        redisTemplate.delete(keys);
      }
    } catch (Exception e) {
      log.error("按模式删除缓存失败: pattern={}", pattern, e);
    }
  }

  /**
   * 检查缓存是否存在
   *
   * @param key 缓存 Key
   * @return 如果 Key 存在返回 true，否则返回 false
   */
  public static boolean exists(String key) {
    try {
      Boolean result = redisTemplate.hasKey(key);
      return result != null && result;
    } catch (Exception e) {
      log.error("检查缓存是否存在失败: key={}", key, e);
      return false;
    }
  }

  /**
   * 设置缓存过期时间
   *
   * @param key 缓存 Key
   * @param seconds 过期时间（秒）
   */
  public static void expire(String key, int seconds) {
    try {
      redisTemplate.expire(key, java.time.Duration.ofSeconds(seconds));
    } catch (Exception e) {
      log.error("设置缓存过期时间失败: key={}, seconds={}", key, seconds, e);
    }
  }

  /**
   * 获取缓存剩余过期时间
   *
   * @param key 缓存 Key
   * @return 剩余过期时间（秒），如果 Key 不存在或没有设置过期时间则返回 -1
   */
  public static long getExpire(String key) {
    try {
      Long ttl = redisTemplate.getExpire(key);
      return ttl != null ? ttl : -1;
    } catch (Exception e) {
      log.error("获取缓存剩余过期时间失败: key={}", key, e);
      return -1;
    }
  }
}

