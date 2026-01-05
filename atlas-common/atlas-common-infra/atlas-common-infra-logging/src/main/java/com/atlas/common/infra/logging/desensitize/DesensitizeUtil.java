/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.desensitize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感信息脱敏工具类
 *
 * <p>提供常见敏感字段的脱敏方法，支持手机号、身份证号、银行卡号、邮箱、密码等字段的脱敏。
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // 脱敏手机号
 * String phone = "13812345678";
 * String maskedPhone = DesensitizeUtil.maskPhone(phone); // 138****5678
 *
 * // 脱敏身份证号
 * String idCard = "440101199001011234";
 * String maskedIdCard = DesensitizeUtil.maskIdCard(idCard); // 440101********1234
 *
 * // 脱敏银行卡号
 * String bankCard = "6222021234567890123";
 * String maskedBankCard = DesensitizeUtil.maskBankCard(bankCard); // ****0123
 *
 * // 脱敏邮箱
 * String email = "testuser@example.com";
 * String maskedEmail = DesensitizeUtil.maskEmail(email); // te****@example.com
 *
 * // 脱敏密码
 * String password = "mypassword123";
 * String maskedPassword = DesensitizeUtil.maskPassword(password); // ******
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public final class DesensitizeUtil {

  /** 私有构造函数，防止实例化 */
  private DesensitizeUtil() {
    throw new UnsupportedOperationException("工具类不允许实例化");
  }

  /**
   * 脱敏手机号
   *
   * <p>保留前 3 位和后 4 位，中间用 {@code ****} 替代。
   *
   * <p>示例：13812345678 → 138****5678
   *
   * @param phone 手机号，如果为 null 或长度小于 7，则返回原值
   * @return 脱敏后的手机号
   */
  public static String maskPhone(String phone) {
    if (phone == null || phone.length() < 7) {
      return phone;
    }
    return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
  }

  /**
   * 脱敏身份证号
   *
   * <p>保留前 6 位和后 4 位，中间用 {@code ****} 替代。
   *
   * <p>示例：440101199001011234 → 440101********1234
   *
   * @param idCard 身份证号，如果为 null 或长度小于 10，则返回原值
   * @return 脱敏后的身份证号
   */
  public static String maskIdCard(String idCard) {
    if (idCard == null || idCard.length() < 10) {
      return idCard;
    }
    return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
  }

  /**
   * 脱敏银行卡号
   *
   * <p>保留后 4 位，前面用 {@code ****} 替代。
   *
   * <p>示例：6222021234567890123 → ****0123
   *
   * @param bankCard 银行卡号，如果为 null 或长度小于 4，则返回原值
   * @return 脱敏后的银行卡号
   */
  public static String maskBankCard(String bankCard) {
    if (bankCard == null || bankCard.length() < 4) {
      return bankCard;
    }
    return "****" + bankCard.substring(bankCard.length() - 4);
  }

  /**
   * 脱敏邮箱
   *
   * <p>保留用户名前 2 位和域名，中间用 {@code ****} 替代。
   *
   * <p>示例：testuser@example.com → te****@example.com
   *
   * @param email 邮箱，如果为 null 或不包含 @，则返回原值
   * @return 脱敏后的邮箱
   */
  public static String maskEmail(String email) {
    if (email == null || !email.contains("@")) {
      return email;
    }
    int atIndex = email.indexOf("@");
    String username = email.substring(0, atIndex);
    String domain = email.substring(atIndex);

    if (username.length() <= 2) {
      return "**" + domain;
    }
    return username.substring(0, 2) + "****" + domain;
  }

  /**
   * 脱敏密码
   *
   * <p>全部替换为 {@code ******}。
   *
   * <p>示例：mypassword123 → ******
   *
   * @param password 密码，如果为 null 或空字符串，则返回原值
   * @return 脱敏后的密码（固定为 ******）
   */
  public static String maskPassword(String password) {
    if (password == null || password.isEmpty()) {
      return password;
    }
    return "******";
  }

  /**
   * 通用脱敏方法
   *
   * <p>根据自定义脱敏规则对值进行脱敏处理。
   *
   * <p>使用示例：
   *
   * <pre>{@code
   * DesensitizeRule rule = DesensitizeRule.builder()
   *     .pattern(Pattern.compile("\\d{4}-\\d{4}-\\d{4}"))
   *     .prefixLength(4)
   *     .suffixLength(4)
   *     .replacement("****")
   *     .build();
   *
   * String masked = DesensitizeUtil.mask("1234-5678-9012", rule); // 1234****9012
   * }</pre>
   *
   * @param value 待脱敏的值
   * @param rule 脱敏规则，如果为 null，则返回原值
   * @return 脱敏后的值
   */
  public static String mask(String value, DesensitizeRule rule) {
    if (value == null || value.isEmpty() || rule == null) {
      return value;
    }

    Pattern pattern = rule.getPattern();
    if (pattern == null) {
      return value;
    }

    Matcher matcher = pattern.matcher(value);
    if (!matcher.find()) {
      return value;
    }

    int prefixLength = rule.getPrefixLength() != null ? rule.getPrefixLength() : 0;
    int suffixLength = rule.getSuffixLength() != null ? rule.getSuffixLength() : 0;
    String replacement = rule.getReplacement() != null ? rule.getReplacement() : "****";

    // 如果 prefixLength + suffixLength >= value.length()，则全部替换
    if (prefixLength + suffixLength >= value.length()) {
      return replacement;
    }

    // 构建脱敏后的字符串
    StringBuilder masked = new StringBuilder();
    if (prefixLength > 0) {
      masked.append(value.substring(0, prefixLength));
    }
    masked.append(replacement);
    if (suffixLength > 0) {
      masked.append(value.substring(value.length() - suffixLength));
    }

    return masked.toString();
  }
}

