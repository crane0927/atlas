/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.desensitize;

import java.util.regex.Pattern;
import lombok.Builder;
import lombok.Data;

/**
 * 脱敏规则配置类
 *
 * <p>定义脱敏规则的匹配模式和替换规则，支持自定义脱敏规则。
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * DesensitizeRule rule = DesensitizeRule.builder()
 *     .fieldType("phone")
 *     .pattern(Pattern.compile("1[3-9]\\d{9}"))
 *     .prefixLength(3)
 *     .suffixLength(4)
 *     .replacement("****")
 *     .build();
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@Builder
public class DesensitizeRule {

  /** 字段类型（phone、idCard、bankCard、email、password等） */
  private String fieldType;

  /** 匹配模式（编译后的正则表达式） */
  private Pattern pattern;

  /** 保留前缀长度 */
  @Builder.Default private Integer prefixLength = 0;

  /** 保留后缀长度 */
  @Builder.Default private Integer suffixLength = 0;

  /** 替换字符串 */
  @Builder.Default private String replacement = "****";
}

