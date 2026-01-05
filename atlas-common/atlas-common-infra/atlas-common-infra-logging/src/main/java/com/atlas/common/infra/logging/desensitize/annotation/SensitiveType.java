/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.desensitize.annotation;

/**
 * 敏感字段类型枚举
 *
 * <p>定义需要脱敏的敏感字段类型，用于 {@link Sensitive} 注解。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public enum SensitiveType {
  /** 手机号 */
  PHONE,

  /** 身份证号 */
  ID_CARD,

  /** 银行卡号 */
  BANK_CARD,

  /** 邮箱 */
  EMAIL,

  /** 密码 */
  PASSWORD,

  /** 自定义类型 */
  CUSTOM
}

