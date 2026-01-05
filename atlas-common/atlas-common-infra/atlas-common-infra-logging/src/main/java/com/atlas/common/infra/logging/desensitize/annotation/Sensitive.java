/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.desensitize.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感字段注解
 *
 * <p>用于标记对象字段需要自动脱敏，支持通过反射自动对字段值进行脱敏处理。
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * public class User {
 *     @Sensitive(type = SensitiveType.PHONE)
 *     private String phone;
 *
 *     @Sensitive(type = SensitiveType.ID_CARD)
 *     private String idCard;
 *
 *     @Sensitive(type = SensitiveType.EMAIL)
 *     private String email;
 * }
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {

  /**
   * 敏感字段类型
   *
   * @return 敏感字段类型
   */
  SensitiveType type();

  /**
   * 保留前缀长度
   *
   * <p>如果未指定，则根据类型自动计算。
   *
   * @return 保留前缀长度，默认 0（自动计算）
   */
  int prefixLength() default 0;

  /**
   * 保留后缀长度
   *
   * <p>如果未指定，则根据类型自动计算。
   *
   * @return 保留后缀长度，默认 0（自动计算）
   */
  int suffixLength() default 0;
}

