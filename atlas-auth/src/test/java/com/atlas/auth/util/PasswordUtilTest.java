/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * PasswordUtil 工具类单元测试
 *
 * <p>测试密码加密和验证功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class PasswordUtilTest {

  private PasswordUtil passwordUtil;

  @BeforeEach
  void setUp() {
    passwordUtil = new PasswordUtil();
  }

  @Test
  void testEncode() {
    // 加密密码
    String rawPassword = "password123";
    String encodedPassword = passwordUtil.encode(rawPassword);

    // 验证加密结果
    assertNotNull(encodedPassword);
    assertNotEquals(rawPassword, encodedPassword);
    assertTrue(encodedPassword.length() > 0);
  }

  @Test
  void testMatches() {
    // 加密密码
    String rawPassword = "password123";
    String encodedPassword = passwordUtil.encode(rawPassword);

    // 验证正确密码
    boolean matches = passwordUtil.matches(rawPassword, encodedPassword);
    assertTrue(matches);

    // 验证错误密码
    boolean notMatches = passwordUtil.matches("wrongpassword", encodedPassword);
    assertFalse(notMatches);
  }

  @Test
  void testEncodeDifferentResults() {
    // 相同密码加密两次，结果应该不同（因为盐值不同）
    String rawPassword = "password123";
    String encodedPassword1 = passwordUtil.encode(rawPassword);
    String encodedPassword2 = passwordUtil.encode(rawPassword);

    // 验证两次加密结果不同
    assertNotEquals(encodedPassword1, encodedPassword2);

    // 但都能验证通过
    assertTrue(passwordUtil.matches(rawPassword, encodedPassword1));
    assertTrue(passwordUtil.matches(rawPassword, encodedPassword2));
  }
}

