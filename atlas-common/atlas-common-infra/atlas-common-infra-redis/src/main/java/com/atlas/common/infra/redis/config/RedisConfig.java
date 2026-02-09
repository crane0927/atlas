/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.redis.config;

import com.atlas.common.infra.redis.util.CacheUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.core.env.Environment;

/**
 * Redis 序列化配置类
 *
 * <p>提供统一的 Redis 序列化配置，确保所有模块使用一致的序列化方式：
 *
 * <ul>
 *   <li>Key 使用 String 序列化（StringRedisSerializer）
 *   <li>Value 使用 JSON 序列化（GenericJackson2JsonRedisSerializer）
 *   <li>Hash Key 和 Hash Value 使用相同的序列化方式
 * </ul>
 *
 * @author Atlas
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

  private final RedisProperties redisProperties;
  private final Environment environment;

  public RedisConfig(RedisProperties redisProperties, Environment environment) {
    this.redisProperties = redisProperties;
    this.environment = environment;
  }

  /**
   * 配置 RedisTemplate Bean
   *
   * <p>设置统一的序列化方式：
   *
   * <ul>
   *   <li>Key 序列化器：StringRedisSerializer
   *   <li>Value 序列化器：GenericJackson2JsonRedisSerializer
   *   <li>Hash Key 序列化器：StringRedisSerializer
   *   <li>Hash Value 序列化器：GenericJackson2JsonRedisSerializer
   * </ul>
   *
   * @param connectionFactory Redis 连接工厂
   * @return 配置好的 RedisTemplate Bean
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Key 序列化器：使用 String 序列化
    RedisSerializer<String> stringSerializer = new StringRedisSerializer();
    template.setKeySerializer(stringSerializer);
    template.setHashKeySerializer(stringSerializer);

    // Value 序列化器：使用 JSON 序列化
    RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
    template.setValueSerializer(jsonSerializer);
    template.setHashValueSerializer(jsonSerializer);

    // 设置默认序列化器
    template.setDefaultSerializer(jsonSerializer);

    // 初始化模板
    template.afterPropertiesSet();

    return template;
  }

  /**
   * 初始化 CacheUtil 的 Key 前缀配置
   *
   * <p>在 Spring 容器初始化完成后，将固定前缀与服务前缀设置到 CacheUtil 中。
   */
  @PostConstruct
  public void initCacheUtil() {
    String servicePrefix = redisProperties.getServicePrefix();
    if (servicePrefix == null || servicePrefix.isBlank()) {
      servicePrefix = environment.getProperty("spring.application.name");
    }
    CacheUtil.initPrefix(redisProperties.getKeyPrefix(), servicePrefix);
  }
}
