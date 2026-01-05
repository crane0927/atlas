/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * FieldError 单元测试
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class FieldErrorTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testBuilder() {
    // Given
    String field = "username";
    String message = "用户名不能为空";

    // When
    FieldError error = FieldError.builder().field(field).message(message).build();

    // Then
    assertNotNull(error);
    assertEquals(field, error.getField());
    assertEquals(message, error.getMessage());
  }

  @Test
  void testNoArgsConstructor() {
    // When
    FieldError error = new FieldError();

    // Then
    assertNotNull(error);
  }

  @Test
  void testAllArgsConstructor() {
    // Given
    String field = "email";
    String message = "邮箱格式不正确";

    // When
    FieldError error = new FieldError(field, message);

    // Then
    assertNotNull(error);
    assertEquals(field, error.getField());
    assertEquals(message, error.getMessage());
  }

  @Test
  void testJsonSerialization() throws Exception {
    // Given
    FieldError error = FieldError.builder().field("username").message("用户名不能为空").build();

    // When
    String json = objectMapper.writeValueAsString(error);

    // Then
    assertNotNull(json);
    assertEquals("{\"field\":\"username\",\"message\":\"用户名不能为空\"}", json);
  }

  @Test
  void testJsonDeserialization() throws Exception {
    // Given
    String json = "{\"field\":\"email\",\"message\":\"邮箱格式不正确\"}";

    // When
    FieldError error = objectMapper.readValue(json, FieldError.class);

    // Then
    assertNotNull(error);
    assertEquals("email", error.getField());
    assertEquals("邮箱格式不正确", error.getMessage());
  }

  @Test
  void testJsonSerializationWithNullValues() throws Exception {
    // Given
    FieldError error = FieldError.builder().field(null).message(null).build();

    // When
    String json = objectMapper.writeValueAsString(error);

    // Then
    assertNotNull(json);
    // null 值会被 @JsonInclude(NON_NULL) 忽略
    assertEquals("{}", json);
  }
}
