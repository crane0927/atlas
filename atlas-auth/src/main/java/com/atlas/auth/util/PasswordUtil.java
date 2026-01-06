/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类
 *
 * <p>提供密码加密和验证功能，使用 BCrypt 算法。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>密码加密（使用 BCrypt 算法）
 *   <li>密码验证（验证明文密码与加密后的密码是否匹配）
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // 加密密码
 * String encodedPassword = passwordUtil.encode("password123");
 *
 * // 验证密码
 * boolean matches = passwordUtil.matches("password123", encodedPassword);
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Component
public class PasswordUtil {

  private final PasswordEncoder passwordEncoder;

  public PasswordUtil() {
    // 使用 BCrypt 算法，强度参数为 10（默认）
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  /**
   * 加密密码
   *
   * <p>使用 BCrypt 算法加密密码，自动生成盐值。
   *
   * @param rawPassword 明文密码
   * @return 加密后的密码
   */
  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  /**
   * 验证密码
   *
   * <p>验证明文密码与加密后的密码是否匹配。
   *
   * @param rawPassword 明文密码
   * @param encodedPassword 加密后的密码
   * @return true 表示密码匹配，false 表示密码不匹配
   */
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}

