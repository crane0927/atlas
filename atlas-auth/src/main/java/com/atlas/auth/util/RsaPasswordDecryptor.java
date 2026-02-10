/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * RSA 密码解密器
 *
 * <p>使用与 JWT 相同的 RSA 私钥，对前端公钥加密后的密码密文进行解密。算法与前端约定：RSA/ECB/OAEPWithSHA-256AndMGF1Padding。
 *
 * <p>前端应使用 GET /api/v1/auth/public-key 获取公钥，对密码加密后 Base64 编码传 encryptedPassword。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class RsaPasswordDecryptor {

  private static final String TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

  private final PrivateKey privateKey;
  private final PublicKey publicKey;

  public RsaPasswordDecryptor(PrivateKey privateKey, PublicKey publicKey) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  /**
   * 解密前端传来的 RSA 加密密码（Base64 密文）
   *
   * @param base64CipherText Base64 编码的密文
   * @return 明文密码，解密失败或参数为空时返回 null（调用方应统一按“用户名或密码错误”处理，避免信息泄露）
   */
  public String decrypt(String base64CipherText) {
    if (base64CipherText == null || base64CipherText.isBlank()) {
      return null;
    }
    try {
      byte[] cipherBytes = Base64.getDecoder().decode(base64CipherText.trim());
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      cipher.init(
              Cipher.DECRYPT_MODE,
              privateKey,
              new OAEPParameterSpec(
                      "SHA-256",
                      "MGF1",
                      MGF1ParameterSpec.SHA256,
                      PSource.PSpecified.DEFAULT
              )
      );
      byte[] plainBytes = cipher.doFinal(cipherBytes);
      return new String(plainBytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.debug("密码解密失败: {}", e.getMessage());
      return null;
    }
  }
}
