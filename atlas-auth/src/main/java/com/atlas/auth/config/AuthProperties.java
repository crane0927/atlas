/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Auth 配置属性类
 *
 * <p>用于读取配置文件中的 Auth 相关配置，支持通过 application.yml 或 Nacos Config 配置 JWT、Token 等参数。
 *
 * <p>配置特性：
 *
 * <ul>
 *   <li>支持从 Nacos Config 读取配置
 *   <li>支持配置动态更新（通过 {@code @ConfigurationProperties} 自动绑定）
 *   <li>配置项符合项目的配置命名规范（{@code atlas.auth.*}）
 * </ul>
 *
 * <p>配置示例：
 *
 * <pre>{@code
 * atlas:
 *   auth:
 *     jwt:
 *       private-key: |
 *         -----BEGIN PRIVATE KEY-----
 *         MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...
 *         -----END PRIVATE KEY-----
 *       public-key: |
 *         -----BEGIN PUBLIC KEY-----
 *         MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
 *         -----END PUBLIC KEY-----
 *       key-id: key-2025-01-06
 *       expire: 7200
 *       algorithm: RS256
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "atlas.auth")
public class AuthProperties {

  /** JWT 配置 */
  private JwtConfig jwt = new JwtConfig();

  /** JWT 配置内部类 */
  @Data
  public static class JwtConfig {

    /** RSA 私钥（PEM 格式） */
    private String privateKey;

    /** RSA 公钥（PEM 格式） */
    private String publicKey;

    /** 密钥ID */
    private String keyId = "key-default";

    /** Token 过期时间（秒，默认 7200） */
    private Long expire = 7200L;

    /** 算法（固定为 RS256） */
    private String algorithm = "RS256";
  }
}

