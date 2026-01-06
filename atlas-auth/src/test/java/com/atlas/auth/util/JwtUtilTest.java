/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.atlas.auth.config.AuthProperties;
import com.atlas.auth.config.JwtConfig;
import com.atlas.auth.model.dto.TokenInfoDTO;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JwtUtil 工具类单元测试
 *
 * <p>测试 JWT Token 的生成、解析和验证功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class JwtUtilTest {

  private JwtUtil jwtUtil;
  private PrivateKey privateKey;
  private PublicKey publicKey;
  private JwtConfig jwtConfig;

  @BeforeEach
  void setUp() throws Exception {
    // 生成测试用的 RSA 密钥对
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    privateKey = keyPair.getPrivate();
    publicKey = keyPair.getPublic();

    // 创建测试配置
    AuthProperties authProperties = new AuthProperties();
    AuthProperties.JwtConfig jwtConfigProps = new AuthProperties.JwtConfig();
    jwtConfigProps.setKeyId("test-key");
    jwtConfigProps.setExpire(7200L);
    jwtConfigProps.setAlgorithm("RS256");
    authProperties.setJwt(jwtConfigProps);

    jwtConfig = new JwtConfig(authProperties) {
      @Override
      public PrivateKey privateKey() {
        return privateKey;
      }

      @Override
      public PublicKey publicKey() {
        return publicKey;
      }

      @Override
      public String getKeyId() {
        return "test-key";
      }

      @Override
      public Long getExpire() {
        return 7200L;
      }

      @Override
      public String getAlgorithm() {
        return "RS256";
      }

      @Override
      public String getPublicKeyPem() {
        return "-----BEGIN PUBLIC KEY-----\n"
            + Base64.getEncoder().encodeToString(publicKey.getEncoded())
            + "\n-----END PUBLIC KEY-----";
      }
    };

    jwtUtil = new JwtUtil(jwtConfig, privateKey, publicKey);
  }

  @Test
  void testGenerateToken() {
    // 准备测试数据
    TokenInfoDTO tokenInfo = new TokenInfoDTO();
    tokenInfo.setUserId(1L);
    tokenInfo.setUsername("admin");
    tokenInfo.setRoles(Arrays.asList("admin", "user"));
    tokenInfo.setPermissions(Arrays.asList("user:read", "user:write"));

    // 生成 Token
    String token = jwtUtil.generateToken(tokenInfo);

    // 验证 Token 不为空
    assertNotNull(token);
    assertTrue(token.length() > 0);
  }

  @Test
  void testParseToken() {
    // 准备测试数据
    TokenInfoDTO tokenInfo = new TokenInfoDTO();
    tokenInfo.setUserId(1L);
    tokenInfo.setUsername("admin");
    tokenInfo.setRoles(Arrays.asList("admin", "user"));
    tokenInfo.setPermissions(Arrays.asList("user:read", "user:write"));

    // 生成 Token
    String token = jwtUtil.generateToken(tokenInfo);

    // 解析 Token
    TokenInfoDTO parsedTokenInfo = jwtUtil.parseToken(token);

    // 验证解析结果
    assertNotNull(parsedTokenInfo);
    assertEquals(tokenInfo.getUserId(), parsedTokenInfo.getUserId());
    assertEquals(tokenInfo.getUsername(), parsedTokenInfo.getUsername());
    assertEquals(tokenInfo.getRoles(), parsedTokenInfo.getRoles());
    assertEquals(tokenInfo.getPermissions(), parsedTokenInfo.getPermissions());
    assertNotNull(parsedTokenInfo.getTokenId());
    assertNotNull(parsedTokenInfo.getIssuedAt());
    assertNotNull(parsedTokenInfo.getExpiresAt());
  }

  @Test
  void testValidateToken() {
    // 准备测试数据
    TokenInfoDTO tokenInfo = new TokenInfoDTO();
    tokenInfo.setUserId(1L);
    tokenInfo.setUsername("admin");
    tokenInfo.setRoles(Arrays.asList("admin", "user"));
    tokenInfo.setPermissions(Arrays.asList("user:read", "user:write"));

    // 生成 Token
    String token = jwtUtil.generateToken(tokenInfo);

    // 验证 Token
    boolean isValid = jwtUtil.validateToken(token);
    assertTrue(isValid);
  }

  @Test
  void testParseTokenWithInvalidToken() {
    // 测试无效 Token
    assertThrows(RuntimeException.class, () -> jwtUtil.parseToken("invalid.token.here"));
  }
}

