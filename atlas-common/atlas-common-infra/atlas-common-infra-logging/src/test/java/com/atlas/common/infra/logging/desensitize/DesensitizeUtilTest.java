/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.desensitize;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** DesensitizeUtil 单元测试 */
class DesensitizeUtilTest {

  @Test
  void testMaskPhone() {
    // Given
    String phone = "13812345678";

    // When
    String masked = DesensitizeUtil.maskPhone(phone);

    // Then
    assertEquals("138****5678", masked);
  }

  @Test
  void testMaskPhoneWithNull() {
    // When
    String masked = DesensitizeUtil.maskPhone(null);

    // Then
    assertNull(masked);
  }

  @Test
  void testMaskPhoneWithShortLength() {
    // Given
    String phone = "1381234"; // 长度小于 7

    // When
    String masked = DesensitizeUtil.maskPhone(phone);

    // Then
    assertEquals(phone, masked);
  }

  @Test
  void testMaskIdCard() {
    // Given
    String idCard = "440101199001011234";

    // When
    String masked = DesensitizeUtil.maskIdCard(idCard);

    // Then
    assertEquals("440101********1234", masked);
  }

  @Test
  void testMaskIdCardWithNull() {
    // When
    String masked = DesensitizeUtil.maskIdCard(null);

    // Then
    assertNull(masked);
  }

  @Test
  void testMaskIdCardWithShortLength() {
    // Given
    String idCard = "440101199"; // 长度小于 10

    // When
    String masked = DesensitizeUtil.maskIdCard(idCard);

    // Then
    assertEquals(idCard, masked);
  }

  @Test
  void testMaskBankCard() {
    // Given
    String bankCard = "6222021234567890123";

    // When
    String masked = DesensitizeUtil.maskBankCard(bankCard);

    // Then
    assertEquals("****0123", masked);
  }

  @Test
  void testMaskBankCardWithNull() {
    // When
    String masked = DesensitizeUtil.maskBankCard(null);

    // Then
    assertNull(masked);
  }

  @Test
  void testMaskBankCardWithShortLength() {
    // Given
    String bankCard = "123"; // 长度小于 4

    // When
    String masked = DesensitizeUtil.maskBankCard(bankCard);

    // Then
    assertEquals(bankCard, masked);
  }

  @Test
  void testMaskEmail() {
    // Given
    String email = "testuser@example.com";

    // When
    String masked = DesensitizeUtil.maskEmail(email);

    // Then
    assertEquals("te****@example.com", masked);
  }

  @Test
  void testMaskEmailWithShortUsername() {
    // Given
    String email = "ab@example.com"; // 用户名长度 <= 2

    // When
    String masked = DesensitizeUtil.maskEmail(email);

    // Then
    assertEquals("**@example.com", masked);
  }

  @Test
  void testMaskEmailWithNull() {
    // When
    String masked = DesensitizeUtil.maskEmail(null);

    // Then
    assertNull(masked);
  }

  @Test
  void testMaskEmailWithoutAt() {
    // Given
    String email = "notanemail";

    // When
    String masked = DesensitizeUtil.maskEmail(email);

    // Then
    assertEquals(email, masked);
  }

  @Test
  void testMaskPassword() {
    // Given
    String password = "mypassword123";

    // When
    String masked = DesensitizeUtil.maskPassword(password);

    // Then
    assertEquals("******", masked);
  }

  @Test
  void testMaskPasswordWithNull() {
    // When
    String masked = DesensitizeUtil.maskPassword(null);

    // Then
    assertNull(masked);
  }

  @Test
  void testMaskPasswordWithEmpty() {
    // Given
    String password = "";

    // When
    String masked = DesensitizeUtil.maskPassword(password);

    // Then
    assertEquals(password, masked);
  }

  @Test
  void testMaskWithCustomRule() {
    // Given
    String value = "1234-5678-9012";
    DesensitizeRule rule =
        DesensitizeRule.builder()
            .pattern(Pattern.compile("\\d{4}-\\d{4}-\\d{4}"))
            .prefixLength(4)
            .suffixLength(4)
            .replacement("****")
            .build();

    // When
    String masked = DesensitizeUtil.mask(value, rule);

    // Then
    assertEquals("1234****9012", masked);
  }

  @Test
  void testMaskWithNullValue() {
    // Given
    DesensitizeRule rule =
        DesensitizeRule.builder()
            .pattern(Pattern.compile("\\d+"))
            .prefixLength(2)
            .suffixLength(2)
            .build();

    // When
    String masked = DesensitizeUtil.mask(null, rule);

    // Then
    assertNull(masked);
  }

  @Test
  void testMaskWithNullRule() {
    // Given
    String value = "1234567890";

    // When
    String masked = DesensitizeUtil.mask(value, null);

    // Then
    assertEquals(value, masked);
  }

  @Test
  void testMaskWithNoMatch() {
    // Given
    String value = "abcdefgh";
    DesensitizeRule rule =
        DesensitizeRule.builder()
            .pattern(Pattern.compile("\\d+"))
            .prefixLength(2)
            .suffixLength(2)
            .build();

    // When
    String masked = DesensitizeUtil.mask(value, rule);

    // Then
    assertEquals(value, masked);
  }

  @Test
  void testMaskWithPrefixAndSuffixExceedingLength() {
    // Given
    String value = "123";
    DesensitizeRule rule =
        DesensitizeRule.builder()
            .pattern(Pattern.compile("\\d+"))
            .prefixLength(2)
            .suffixLength(2)
            .replacement("****")
            .build();

    // When
    String masked = DesensitizeUtil.mask(value, rule);

    // Then
    assertEquals("****", masked);
  }
}

