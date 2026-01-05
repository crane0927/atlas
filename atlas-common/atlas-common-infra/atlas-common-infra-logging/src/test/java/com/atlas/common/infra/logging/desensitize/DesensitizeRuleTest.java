/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.desensitize;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** DesensitizeRule 单元测试 */
class DesensitizeRuleTest {

  @Test
  void testBuilder() {
    // Given
    String fieldType = "phone";
    Pattern pattern = Pattern.compile("1[3-9]\\d{9}");
    Integer prefixLength = 3;
    Integer suffixLength = 4;
    String replacement = "****";

    // When
    DesensitizeRule rule =
        DesensitizeRule.builder()
            .fieldType(fieldType)
            .pattern(pattern)
            .prefixLength(prefixLength)
            .suffixLength(suffixLength)
            .replacement(replacement)
            .build();

    // Then
    assertNotNull(rule);
    assertEquals(fieldType, rule.getFieldType());
    assertEquals(pattern, rule.getPattern());
    assertEquals(prefixLength, rule.getPrefixLength());
    assertEquals(suffixLength, rule.getSuffixLength());
    assertEquals(replacement, rule.getReplacement());
  }

  @Test
  void testBuilderWithDefaults() {
    // When
    DesensitizeRule rule =
        DesensitizeRule.builder()
            .fieldType("custom")
            .pattern(Pattern.compile("\\d+"))
            .build();

    // Then
    assertNotNull(rule);
    assertEquals("custom", rule.getFieldType());
    assertNotNull(rule.getPattern());
    assertEquals(0, rule.getPrefixLength());
    assertEquals(0, rule.getSuffixLength());
    assertEquals("****", rule.getReplacement());
  }

  @Test
  void testEqualsAndHashCode() {
    // Given
    Pattern pattern = Pattern.compile("\\d+");
    DesensitizeRule rule1 =
        DesensitizeRule.builder()
            .fieldType("test")
            .pattern(pattern)
            .prefixLength(2)
            .suffixLength(2)
            .build();

    DesensitizeRule rule2 =
        DesensitizeRule.builder()
            .fieldType("test")
            .pattern(pattern)
            .prefixLength(2)
            .suffixLength(2)
            .build();

    // Then
    assertEquals(rule1, rule2);
    assertEquals(rule1.hashCode(), rule2.hashCode());
  }
}

