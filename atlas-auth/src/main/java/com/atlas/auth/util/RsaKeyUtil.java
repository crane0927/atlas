/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 密钥工具类
 *
 * <p>提供 RSA 非对称密钥对的生成、PEM 编码与解析，供本地开发、密钥轮换及配置初始化使用。
 *
 * <p>PEM 格式与 {@link com.atlas.auth.config.JwtConfig} 一致：
 *
 * <ul>
 *   <li>私钥：PKCS#8（{@code -----BEGIN PRIVATE KEY-----}）
 *   <li>公钥：X.509（{@code -----BEGIN PUBLIC KEY-----}）
 * </ul>
 *
 * <p>使用示例：
 *
 * <p>命令行生成密钥对：
 *
 * <pre>{@code
 * mvn -q -pl atlas-auth exec:java -Dexec.mainClass=com.atlas.auth.util.RsaKeyUtil
 * mvn -q -pl atlas-auth exec:java -Dexec.mainClass=com.atlas.auth.util.RsaKeyUtil -Dexec.args=4096
 * }</pre>
 *
 * <p>代码调用示例：
 *
 * <pre>{@code
 * RsaKeyUtil.RsaKeyPairPem keyPair = RsaKeyUtil.generateKeyPairPem();
 * String privateKeyPem = keyPair.privateKeyPem();
 * String publicKeyPem = keyPair.publicKeyPem();
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public final class RsaKeyUtil {

  private static final String ALGORITHM = "RSA";

  private static final String PRIVATE_KEY_PEM_TYPE = "PRIVATE KEY";

  private static final String PUBLIC_KEY_PEM_TYPE = "PUBLIC KEY";

  /** 默认 RSA 密钥长度（位） */
  public static final int DEFAULT_KEY_SIZE = 2048;

  /** RSA 密钥对 PEM 结果 */
  public record RsaKeyPairPem(String privateKeyPem, String publicKeyPem) {}

  private RsaKeyUtil() {
    throw new UnsupportedOperationException("工具类不允许实例化");
  }

  /**
   * 生成 RSA 密钥对（默认 2048 位）
   *
   * @return RSA 密钥对
   */
  public static KeyPair generateKeyPair() {
    return generateKeyPair(DEFAULT_KEY_SIZE);
  }

  /**
   * 生成指定长度的 RSA 密钥对
   *
   * @param keySize 密钥长度（位），建议不小于 2048
   * @return RSA 密钥对
   */
  public static KeyPair generateKeyPair(int keySize) {
    if (keySize < 2048) {
      throw new IllegalArgumentException("RSA 密钥长度不能小于 2048 位");
    }
    try {
      KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
      generator.initialize(keySize);
      return generator.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("生成 RSA 密钥对失败: " + e.getMessage(), e);
    }
  }

  /**
   * 生成 RSA 密钥对并返回 PEM 格式字符串
   *
   * @return 私钥与公钥的 PEM 字符串
   */
  public static RsaKeyPairPem generateKeyPairPem() {
    return toPem(generateKeyPair());
  }

  /**
   * 生成指定长度的 RSA 密钥对并返回 PEM 格式字符串
   *
   * @param keySize 密钥长度（位），建议不小于 2048
   * @return 私钥与公钥的 PEM 字符串
   */
  public static RsaKeyPairPem generateKeyPairPem(int keySize) {
    return toPem(generateKeyPair(keySize));
  }

  /**
   * 将密钥对转换为 PEM 格式字符串
   *
   * @param keyPair RSA 密钥对
   * @return 私钥与公钥的 PEM 字符串
   */
  public static RsaKeyPairPem toPem(KeyPair keyPair) {
    return new RsaKeyPairPem(
        toPrivateKeyPem(keyPair.getPrivate()), toPublicKeyPem(keyPair.getPublic()));
  }

  /**
   * 将私钥编码为 PKCS#8 PEM 字符串
   *
   * @param privateKey RSA 私钥
   * @return PEM 格式私钥
   */
  public static String toPrivateKeyPem(PrivateKey privateKey) {
    return encodePem(PRIVATE_KEY_PEM_TYPE, privateKey.getEncoded());
  }

  /**
   * 将公钥编码为 X.509 PEM 字符串
   *
   * @param publicKey RSA 公钥
   * @return PEM 格式公钥
   */
  public static String toPublicKeyPem(PublicKey publicKey) {
    return encodePem(PUBLIC_KEY_PEM_TYPE, publicKey.getEncoded());
  }

  /**
   * 从 PEM 字符串解析 RSA 私钥
   *
   * @param privateKeyPem PEM 格式私钥
   * @return RSA 私钥
   */
  public static PrivateKey fromPrivateKeyPem(String privateKeyPem) {
    try {
      byte[] keyBytes = decodePem(privateKeyPem, PRIVATE_KEY_PEM_TYPE);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
      return keyFactory.generatePrivate(keySpec);
    } catch (Exception e) {
      throw new IllegalArgumentException("解析 RSA 私钥失败: " + e.getMessage(), e);
    }
  }

  /**
   * 从 PEM 字符串解析 RSA 公钥
   *
   * @param publicKeyPem PEM 格式公钥
   * @return RSA 公钥
   */
  public static PublicKey fromPublicKeyPem(String publicKeyPem) {
    try {
      byte[] keyBytes = decodePem(publicKeyPem, PUBLIC_KEY_PEM_TYPE);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
      return keyFactory.generatePublic(keySpec);
    } catch (Exception e) {
      throw new IllegalArgumentException("解析 RSA 公钥失败: " + e.getMessage(), e);
    }
  }

  private static String encodePem(String type, byte[] encoded) {
    String base64 = Base64.getEncoder().encodeToString(encoded);
    StringBuilder pem = new StringBuilder();
    pem.append("-----BEGIN ").append(type).append("-----\n");
    for (int index = 0; index < base64.length(); index += 64) {
      pem.append(base64, index, Math.min(index + 64, base64.length())).append('\n');
    }
    pem.append("-----END ").append(type).append("-----");
    return pem.toString();
  }

  private static byte[] decodePem(String pem, String type) {
    if (pem == null || pem.isBlank()) {
      throw new IllegalArgumentException("PEM 内容不能为空");
    }
    String beginMarker = "-----BEGIN " + type + "-----";
    String endMarker = "-----END " + type + "-----";
    String content =
        pem.replace("\\n", "\n")
            .replace("\\M", "\nM")
            .replace(beginMarker, "")
            .replace(endMarker, "")
            .replaceAll("\\s", "");
    return Base64.getDecoder().decode(content);
  }

  /**
   * 命令行入口：生成 RSA 密钥对并输出 PEM，可直接复制到 Nacos 配置。
   *
   * <p>可选参数：密钥长度（位），默认 2048。示例：{@code java RsaKeyUtil 4096}
   *
   * @param args 命令行参数，第一个参数为密钥长度（可选）
   */
  public static void main(String[] args) {
    int keySize = DEFAULT_KEY_SIZE;
    if (args.length > 0) {
      keySize = Integer.parseInt(args[0]);
    }

    RsaKeyPairPem keyPair = generateKeyPairPem(keySize);

    System.out.println("# RSA 密钥对（" + keySize + " 位），复制到 atlas.auth.jwt 配置");
    System.out.println("private-key: |");
    for (String line : keyPair.privateKeyPem().split("\n")) {
      System.out.println("  " + line);
    }
    System.out.println("public-key: |");
    for (String line : keyPair.publicKeyPem().split("\n")) {
      System.out.println("  " + line);
    }
  }
}
