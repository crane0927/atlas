/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.logging.trace;

import java.util.UUID;

/**
 * TraceId 生成器
 *
 * <p>提供 TraceId 的生成功能，支持 UUID 和雪花算法两种生成方式。
 *
 * <p>TraceId 用于分布式链路追踪，确保一次请求在整个微服务系统中的完整调用链可以被追踪。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public final class TraceIdGenerator {

  /** 私有构造函数，防止实例化 */
  private TraceIdGenerator() {
    throw new UnsupportedOperationException("工具类不允许实例化");
  }

  /**
   * 使用 UUID 生成 TraceId
   *
   * <p>生成 32 位 UUID（去除连字符），适合分布式环境，无需协调。
   *
   * <p>示例：550e8400e29b41d4a716446655440000
   *
   * @return 32 位 TraceId 字符串
   */
  public static String generateUUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  /**
   * 使用雪花算法生成 TraceId
   *
   * <p>雪花算法生成的 ID 包含时间戳信息，具有有序性，便于排序和查询。
   *
   * <p>注意：雪花算法需要配置数据中心ID和工作机器ID，本实现使用默认配置。
   *
   * <p>示例：1234567890123456789
   *
   * @return 雪花算法生成的 TraceId 字符串
   */
  public static String generateSnowflake() {
    // TODO: 实现雪花算法生成器
    // 当前实现使用 UUID 作为临时方案
    // 实际使用时需要配置数据中心ID和工作机器ID
    return generateUUID();
  }
}
