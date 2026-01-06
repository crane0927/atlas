/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.config;

import io.jsonwebtoken.security.Keys;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置类
 *
 * <p>配置 JWT Token 的密钥对、过期时间等参数。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>从 Nacos Config 读取 RSA 密钥对（PEM 格式）
 *   <li>将 PEM 格式密钥转换为 Java Key 对象
 *   <li>提供私钥和公钥 Bean，供 JWT 工具类使用
 *   <li>支持密钥轮换（通过配置更新）
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
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class JwtConfig {

  private final AuthProperties authProperties;

  public JwtConfig(AuthProperties authProperties) {
    this.authProperties = authProperties;
  }

  /**
   * 创建 RSA 私钥 Bean
   *
   * <p>从配置中读取 PEM 格式的私钥，转换为 Java PrivateKey 对象。
   *
   * @return RSA 私钥
   */
  @Bean
  public PrivateKey privateKey() {
    try {
      String privateKeyPem = authProperties.getJwt().getPrivateKey();
      if (privateKeyPem == null || privateKeyPem.trim().isEmpty()) {
        throw new IllegalStateException("JWT 私钥未配置，请在 Nacos Config 中配置 atlas.auth.jwt.private-key");
      }

      // 移除 PEM 格式的头部和尾部
      String privateKeyContent =
          privateKeyPem
              .replace("-----BEGIN PRIVATE KEY-----", "")
              .replace("-----END PRIVATE KEY-----", "")
              .replaceAll("\\s", "");

      // Base64 解码
      byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);

      // 转换为 PrivateKey
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePrivate(keySpec);
    } catch (Exception e) {
      log.error("解析 JWT 私钥失败", e);
      throw new IllegalStateException("解析 JWT 私钥失败: " + e.getMessage(), e);
    }
  }

  /**
   * 创建 RSA 公钥 Bean
   *
   * <p>从配置中读取 PEM 格式的公钥，转换为 Java PublicKey 对象。
   *
   * @return RSA 公钥
   */
  @Bean
  public PublicKey publicKey() {
    try {
      String publicKeyPem = authProperties.getJwt().getPublicKey();
      if (publicKeyPem == null || publicKeyPem.trim().isEmpty()) {
        throw new IllegalStateException("JWT 公钥未配置，请在 Nacos Config 中配置 atlas.auth.jwt.public-key");
      }

      // 移除 PEM 格式的头部和尾部
      String publicKeyContent =
          publicKeyPem
              .replace("-----BEGIN PUBLIC KEY-----", "")
              .replace("-----END PUBLIC KEY-----", "")
              .replaceAll("\\s", "");

      // Base64 解码
      byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);

      // 转换为 PublicKey
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePublic(keySpec);
    } catch (Exception e) {
      log.error("解析 JWT 公钥失败", e);
      throw new IllegalStateException("解析 JWT 公钥失败: " + e.getMessage(), e);
    }
  }

  /**
   * 获取密钥ID
   *
   * @return 密钥ID
   */
  public String getKeyId() {
    return authProperties.getJwt().getKeyId();
  }

  /**
   * 获取 Token 过期时间（秒）
   *
   * @return Token 过期时间（秒）
   */
  public Long getExpire() {
    return authProperties.getJwt().getExpire();
  }

  /**
   * 获取算法
   *
   * @return 算法名称
   */
  public String getAlgorithm() {
    return authProperties.getJwt().getAlgorithm();
  }

  /**
   * 获取公钥（PEM 格式）
   *
   * <p>用于 Gateway 获取公钥接口。
   *
   * @return 公钥（PEM 格式）
   */
  public String getPublicKeyPem() {
    return authProperties.getJwt().getPublicKey();
  }
}

