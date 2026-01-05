/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.logging.desensitize;

import com.atlas.common.infra.logging.desensitize.annotation.Sensitive;
import com.atlas.common.infra.logging.desensitize.annotation.SensitiveType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 脱敏拦截器
 *
 * <p>提供日志消息和对象字段的自动脱敏功能：
 *
 * <ul>
 *   <li>日志消息脱敏：检测日志消息中的敏感信息模式，自动应用脱敏规则
 *   <li>对象字段脱敏：通过反射和 {@link Sensitive} 注解，自动对对象字段进行脱敏
 *   <li>自定义规则：支持配置自定义脱敏规则
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // 日志消息脱敏
 * String message = "用户注册成功，手机号: 13812345678";
 * String maskedMessage = DesensitizeInterceptor.maskMessage(message);
 * // 输出: 用户注册成功，手机号: 138****5678
 *
 * // 对象字段脱敏
 * User user = new User();
 * user.setPhone("13812345678");
 * DesensitizeInterceptor.maskObject(user);
 * // user.getPhone() 返回: 138****5678
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public final class DesensitizeInterceptor {

  /** 手机号正则表达式 */
  private static final Pattern PHONE_PATTERN = Pattern.compile("1[3-9]\\d{9}");

  /** 身份证号正则表达式 */
  private static final Pattern ID_CARD_PATTERN = Pattern.compile("\\d{15}|\\d{17}[\\dXx]");

  /** 银行卡号正则表达式 */
  private static final Pattern BANK_CARD_PATTERN = Pattern.compile("\\d{16,19}");

  /** 邮箱正则表达式 */
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

  /** 自定义脱敏规则列表 */
  private static final List<DesensitizeRule> CUSTOM_RULES = new ArrayList<>();

  /** 私有构造函数，防止实例化 */
  private DesensitizeInterceptor() {
    throw new UnsupportedOperationException("工具类不允许实例化");
  }

  /**
   * 添加自定义脱敏规则
   *
   * @param rule 脱敏规则
   */
  public static void addCustomRule(DesensitizeRule rule) {
    if (rule != null) {
      CUSTOM_RULES.add(rule);
    }
  }

  /** 清除所有自定义脱敏规则 */
  public static void clearCustomRules() {
    CUSTOM_RULES.clear();
  }

  /**
   * 对日志消息进行脱敏处理
   *
   * <p>检测日志消息中的敏感信息模式（手机号、身份证号、银行卡号、邮箱等），自动应用脱敏规则。
   *
   * @param message 日志消息
   * @return 脱敏后的日志消息
   */
  public static String maskMessage(String message) {
    if (message == null || message.isEmpty()) {
      return message;
    }

    String result = message;

    // 脱敏手机号
    result =
        PHONE_PATTERN
            .matcher(result)
            .replaceAll(
                matchResult -> {
                  String phone = matchResult.group();
                  return DesensitizeUtil.maskPhone(phone);
                });

    // 脱敏身份证号
    result =
        ID_CARD_PATTERN
            .matcher(result)
            .replaceAll(
                matchResult -> {
                  String idCard = matchResult.group();
                  return DesensitizeUtil.maskIdCard(idCard);
                });

    // 脱敏银行卡号
    result =
        BANK_CARD_PATTERN
            .matcher(result)
            .replaceAll(
                matchResult -> {
                  String bankCard = matchResult.group();
                  return DesensitizeUtil.maskBankCard(bankCard);
                });

    // 脱敏邮箱
    result =
        EMAIL_PATTERN
            .matcher(result)
            .replaceAll(
                matchResult -> {
                  String email = matchResult.group();
                  return DesensitizeUtil.maskEmail(email);
                });

    // 应用自定义规则
    for (DesensitizeRule rule : CUSTOM_RULES) {
      if (rule.getPattern() != null) {
        result =
            rule.getPattern()
                .matcher(result)
                .replaceAll(
                    matchResult -> {
                      return DesensitizeUtil.mask(matchResult.group(), rule);
                    });
      }
    }

    return result;
  }

  /**
   * 对对象字段进行自动脱敏处理
   *
   * <p>通过反射扫描对象字段上的 {@link Sensitive} 注解，自动对字段值进行脱敏。
   *
   * @param obj 待脱敏的对象
   */
  public static void maskObject(Object obj) {
    if (obj == null) {
      return;
    }

    Class<?> clazz = obj.getClass();
    Field[] fields = clazz.getDeclaredFields();

    for (Field field : fields) {
      Sensitive sensitive = field.getAnnotation(Sensitive.class);
      if (sensitive == null) {
        continue;
      }

      try {
        field.setAccessible(true);
        Object value = field.get(obj);

        if (value == null) {
          continue;
        }

        String stringValue = value.toString();
        String maskedValue = maskFieldValue(stringValue, sensitive);

        // 设置脱敏后的值
        field.set(obj, maskedValue);
      } catch (IllegalAccessException e) {
        // 忽略无法访问的字段
      }
    }
  }

  /**
   * 根据 {@link Sensitive} 注解对字段值进行脱敏
   *
   * @param value 字段值
   * @param sensitive 敏感字段注解
   * @return 脱敏后的值
   */
  private static String maskFieldValue(String value, Sensitive sensitive) {
    if (value == null || value.isEmpty()) {
      return value;
    }

    SensitiveType type = sensitive.type();
    int prefixLength = sensitive.prefixLength();
    int suffixLength = sensitive.suffixLength();

    // 如果指定了 prefixLength 和 suffixLength，使用自定义规则
    if (prefixLength > 0 || suffixLength > 0) {
      DesensitizeRule rule =
          DesensitizeRule.builder()
              .prefixLength(prefixLength)
              .suffixLength(suffixLength)
              .replacement("****")
              .build();
      return DesensitizeUtil.mask(value, rule);
    }

    // 根据类型使用默认脱敏规则
    switch (type) {
      case PHONE:
        return DesensitizeUtil.maskPhone(value);
      case ID_CARD:
        return DesensitizeUtil.maskIdCard(value);
      case BANK_CARD:
        return DesensitizeUtil.maskBankCard(value);
      case EMAIL:
        return DesensitizeUtil.maskEmail(value);
      case PASSWORD:
        return DesensitizeUtil.maskPassword(value);
      case CUSTOM:
      default:
        return value;
    }
  }
}
