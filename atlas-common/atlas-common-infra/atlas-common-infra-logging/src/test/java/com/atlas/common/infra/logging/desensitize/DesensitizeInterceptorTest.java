/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.desensitize;

import static org.junit.jupiter.api.Assertions.*;

import com.atlas.common.infra.logging.desensitize.annotation.Sensitive;
import com.atlas.common.infra.logging.desensitize.annotation.SensitiveType;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** DesensitizeInterceptor 单元测试 */
class DesensitizeInterceptorTest {

  /** 测试用的用户类 */
  static class TestUser {
    @Sensitive(type = SensitiveType.PHONE)
    private String phone;

    @Sensitive(type = SensitiveType.ID_CARD)
    private String idCard;

    @Sensitive(type = SensitiveType.EMAIL)
    private String email;

    @Sensitive(type = SensitiveType.PASSWORD)
    private String password;

    @Sensitive(type = SensitiveType.CUSTOM, prefixLength = 2, suffixLength = 2)
    private String customField;

    public String getPhone() {
      return phone;
    }

    public void setPhone(String phone) {
      this.phone = phone;
    }

    public String getIdCard() {
      return idCard;
    }

    public void setIdCard(String idCard) {
      this.idCard = idCard;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getCustomField() {
      return customField;
    }

    public void setCustomField(String customField) {
      this.customField = customField;
    }
  }

  @BeforeEach
  void setUp() {
    DesensitizeInterceptor.clearCustomRules();
  }

  @AfterEach
  void tearDown() {
    DesensitizeInterceptor.clearCustomRules();
  }

  @Test
  void testMaskMessageWithPhone() {
    // Given
    String message = "用户注册成功，手机号: 13812345678";

    // When
    String masked = DesensitizeInterceptor.maskMessage(message);

    // Then
    assertTrue(masked.contains("138****5678"));
    assertFalse(masked.contains("13812345678"));
  }

  @Test
  void testMaskMessageWithIdCard() {
    // Given
    String message = "用户信息，身份证号: 440101199001011234";

    // When
    String masked = DesensitizeInterceptor.maskMessage(message);

    // Then
    assertTrue(masked.contains("440101********1234"));
    assertFalse(masked.contains("440101199001011234"));
  }

  @Test
  void testMaskMessageWithEmail() {
    // Given
    String message = "用户邮箱: testuser@example.com";

    // When
    String masked = DesensitizeInterceptor.maskMessage(message);

    // Then
    assertTrue(masked.contains("te****@example.com"));
    assertFalse(masked.contains("testuser@example.com"));
  }

  @Test
  void testMaskMessageWithMultipleSensitiveInfo() {
    // Given
    String message = "用户注册，手机号: 13812345678, 邮箱: testuser@example.com";

    // When
    String masked = DesensitizeInterceptor.maskMessage(message);

    // Then
    assertTrue(masked.contains("138****5678"));
    assertTrue(masked.contains("te****@example.com"));
    assertFalse(masked.contains("13812345678"));
    assertFalse(masked.contains("testuser@example.com"));
  }

  @Test
  void testMaskMessageWithNull() {
    // When
    String masked = DesensitizeInterceptor.maskMessage(null);

    // Then
    assertNull(masked);
  }

  @Test
  void testMaskMessageWithEmpty() {
    // Given
    String message = "";

    // When
    String masked = DesensitizeInterceptor.maskMessage(message);

    // Then
    assertEquals(message, masked);
  }

  @Test
  void testMaskObjectWithPhone() {
    // Given
    TestUser user = new TestUser();
    user.setPhone("13812345678");

    // When
    DesensitizeInterceptor.maskObject(user);

    // Then
    assertEquals("138****5678", user.getPhone());
  }

  @Test
  void testMaskObjectWithIdCard() {
    // Given
    TestUser user = new TestUser();
    user.setIdCard("440101199001011234");

    // When
    DesensitizeInterceptor.maskObject(user);

    // Then
    assertEquals("440101********1234", user.getIdCard());
  }

  @Test
  void testMaskObjectWithEmail() {
    // Given
    TestUser user = new TestUser();
    user.setEmail("testuser@example.com");

    // When
    DesensitizeInterceptor.maskObject(user);

    // Then
    assertEquals("te****@example.com", user.getEmail());
  }

  @Test
  void testMaskObjectWithPassword() {
    // Given
    TestUser user = new TestUser();
    user.setPassword("mypassword123");

    // When
    DesensitizeInterceptor.maskObject(user);

    // Then
    assertEquals("******", user.getPassword());
  }

  @Test
  void testMaskObjectWithCustomField() {
    // Given
    TestUser user = new TestUser();
    user.setCustomField("1234567890");

    // When
    DesensitizeInterceptor.maskObject(user);

    // Then
    assertEquals("12****90", user.getCustomField());
  }

  @Test
  void testMaskObjectWithNull() {
    // When & Then - 不应该抛出异常
    assertDoesNotThrow(() -> DesensitizeInterceptor.maskObject(null));
  }

  @Test
  void testMaskObjectWithNullField() {
    // Given
    TestUser user = new TestUser();
    user.setPhone(null);

    // When
    DesensitizeInterceptor.maskObject(user);

    // Then
    assertNull(user.getPhone());
  }

  @Test
  void testAddCustomRule() {
    // Given
    DesensitizeRule rule =
        DesensitizeRule.builder()
            .fieldType("custom")
            .pattern(Pattern.compile("\\d{4}-\\d{4}"))
            .prefixLength(4)
            .suffixLength(4)
            .replacement("****")
            .build();

    // When
    DesensitizeInterceptor.addCustomRule(rule);
    String message = "卡号: 1234-5678";
    String masked = DesensitizeInterceptor.maskMessage(message);

    // Then
    assertTrue(masked.contains("1234****5678"));
  }

  @Test
  void testClearCustomRules() {
    // Given
    DesensitizeRule rule =
        DesensitizeRule.builder()
            .fieldType("custom")
            .pattern(Pattern.compile("\\d+"))
            .build();
    DesensitizeInterceptor.addCustomRule(rule);

    // When
    DesensitizeInterceptor.clearCustomRules();
    String message = "数字: 1234567890";
    String masked = DesensitizeInterceptor.maskMessage(message);

    // Then
    assertEquals(message, masked);
  }
}

