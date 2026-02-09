/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.config;

import com.atlas.gateway.filter.GatewayTokenValidator;
import com.atlas.gateway.filter.JwtGatewayTokenValidator;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Gateway JWT 配置
 *
 * <p>当校验方式为 jwt 且配置了非空的 {@code atlas.gateway.auth.jwt.public-key} 时，解析公钥并注册
 * JwtGatewayTokenValidator 为 @Primary。校验方式为 introspection 时本配置不加载。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnExpression(
    "!'${atlas.gateway.auth.jwt.public-key:}'.trim().isEmpty() "
        + "and '${atlas.gateway.auth.validation-mode:jwt}' != 'introspection'")
public class GatewayJwtConfiguration {

  @Bean
  public PublicKey gatewayJwtPublicKey(GatewayProperties gatewayProperties) {
    String publicKeyPem =
        gatewayProperties.getAuth().getJwt().getPublicKey();
    if (publicKeyPem == null || publicKeyPem.trim().isEmpty()) {
      throw new IllegalStateException(
          "atlas.gateway.auth.jwt.public-key 已启用但未配置，请在 Nacos 或 application.yml 中配置");
    }
    try {
      publicKeyPem = publicKeyPem.replace("\\n", "\n").replace("\\M", "\nM");
      String publicKeyContent =
          publicKeyPem
              .replace("-----BEGIN PUBLIC KEY-----", "")
              .replace("-----END PUBLIC KEY-----", "")
              .replaceAll("\\s", "");
      byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePublic(keySpec);
    } catch (Exception e) {
      log.error("解析 Gateway JWT 公钥失败", e);
      throw new IllegalStateException("解析 Gateway JWT 公钥失败: " + e.getMessage(), e);
    }
  }

  @Bean
  @Primary
  public GatewayTokenValidator jwtGatewayTokenValidator(
      PublicKey gatewayJwtPublicKey, GatewayProperties gatewayProperties) {
    return new JwtGatewayTokenValidator(gatewayJwtPublicKey, gatewayProperties);
  }
}
