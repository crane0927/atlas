/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.redis.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis 自动配置类。
 *
 * <p>为 Spring Boot 应用自动注册 Redis 配置，包括：
 *
 * <ul>
 *   <li>RedisTemplate Bean 配置（统一的序列化方式）
 *   <li>CacheUtil 组件注册（含前缀初始化）
 * </ul>
 *
 * <p>当依赖缺失时，自动配置不会生效，服务回退到默认 Redis 配置。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(RedisTemplate.class)
@Import({RedisConfig.class})
@ComponentScan(basePackages = "com.atlas.common.infra.redis")
public class RedisAutoConfiguration {}
