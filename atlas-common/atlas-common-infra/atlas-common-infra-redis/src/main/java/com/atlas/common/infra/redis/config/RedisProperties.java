/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 配置属性类
 *
 * <p>用于读取配置文件中的 Redis 相关配置，支持通过 application.yml 配置 Key 前缀等参数。
 *
 * @author Atlas
 */
@Data
@ConfigurationProperties(prefix = "atlas.redis")
public class RedisProperties {

  /** Key 前缀，默认值为 "atlas" */
  private String keyPrefix = "atlas";
}
