/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.redis.key;

import com.atlas.common.infra.redis.config.RedisProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Redis Key 命名规范工具类
 *
 * <p>提供统一的 Redis Key 命名规范，使用 Builder 模式支持链式调用。
 *
 * <p>Key 格式：{prefix}:{module}:{business}:{id}
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // 构建 Key
 * String key = RedisKeyBuilder.builder()
 *     .module("user")
 *     .business("info")
 *     .id("123")
 *     .build();
 * // 结果: "atlas:user:info:123"
 *
 * // 设置过期时间（仅用于标记，实际过期时间需要在 CacheUtil 中设置）
 * RedisKeyBuilder builder = RedisKeyBuilder.builder()
 *     .module("user")
 *     .business("info")
 *     .id("123")
 *     .withTtl(3600);  // 1 小时过期
 * String key = builder.build();
 * }</pre>
 *
 * @author Atlas
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKeyBuilder {

  /** Key 前缀，默认值为 "atlas" */
  private static String keyPrefix = "atlas";

  private String module;
  private String business;
  private String id;
  private Integer ttl;

  /**
   * 创建 Key 构建器
   *
   * @return RedisKeyBuilder 实例
   */
  public static RedisKeyBuilder builder() {
    return new RedisKeyBuilder();
  }

  /**
   * 初始化 Key 前缀（由 Spring 自动调用）
   *
   * @param properties Redis 配置属性
   */
  public static void init(RedisProperties properties) {
    if (properties != null && properties.getKeyPrefix() != null) {
      keyPrefix = properties.getKeyPrefix();
    }
  }

  /**
   * 设置模块名
   *
   * @param module 模块名
   * @return RedisKeyBuilder 实例，支持链式调用
   */
  public RedisKeyBuilder module(String module) {
    this.module = module;
    return this;
  }

  /**
   * 设置业务标识
   *
   * @param business 业务标识
   * @return RedisKeyBuilder 实例，支持链式调用
   */
  public RedisKeyBuilder business(String business) {
    this.business = business;
    return this;
  }

  /**
   * 设置唯一标识
   *
   * @param id 唯一标识
   * @return RedisKeyBuilder 实例，支持链式调用
   */
  public RedisKeyBuilder id(String id) {
    this.id = id;
    return this;
  }

  /**
   * 设置 Key 过期时间（秒）
   *
   * <p>注意：此方法仅用于标记，实际过期时间需要在 CacheUtil 中设置。
   *
   * @param seconds 过期时间（秒）
   * @return RedisKeyBuilder 实例，支持链式调用
   */
  public RedisKeyBuilder withTtl(int seconds) {
    this.ttl = seconds;
    return this;
  }

  /**
   * 获取设置的过期时间（秒）
   *
   * @return 过期时间（秒），如果未设置则返回 null
   */
  public Integer getTtl() {
    return ttl;
  }

  /**
   * 构建完整的 Key 字符串
   *
   * <p>Key 格式：{prefix}:{module}:{business}:{id}
   *
   * @return 完整的 Key 字符串
   * @throws IllegalArgumentException 如果 module、business 或 id 为 null 或空字符串
   */
  public String build() {
    validate();
    return String.join(":", keyPrefix, module, business, id);
  }

  /**
   * 验证必填字段
   *
   * @throws IllegalArgumentException 如果 module、business 或 id 为 null 或空字符串
   */
  private void validate() {
    if (module == null || module.trim().isEmpty()) {
      throw new IllegalArgumentException("module 不能为空");
    }
    if (business == null || business.trim().isEmpty()) {
      throw new IllegalArgumentException("business 不能为空");
    }
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("id 不能为空");
    }
  }
}

