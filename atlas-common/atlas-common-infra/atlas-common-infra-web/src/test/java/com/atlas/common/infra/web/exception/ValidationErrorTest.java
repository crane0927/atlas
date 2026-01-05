/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

/**
 * ValidationError 单元测试
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class ValidationErrorTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void testBuilder() {
    // Given
    List<FieldError> errors = new ArrayList<>();
    errors.add(FieldError.builder().field("username").message("用户名不能为空").build());
    errors.add(FieldError.builder().field("email").message("邮箱格式不正确").build());

    // When
    ValidationError validationError = ValidationError.builder().errors(errors).build();

    // Then
    assertNotNull(validationError);
    assertNotNull(validationError.getErrors());
    assertEquals(2, validationError.getErrors().size());
    assertEquals("username", validationError.getErrors().get(0).getField());
    assertEquals("邮箱格式不正确", validationError.getErrors().get(1).getMessage());
  }

  @Test
  void testNoArgsConstructor() {
    // When
    ValidationError validationError = new ValidationError();

    // Then
    assertNotNull(validationError);
  }

  @Test
  void testAllArgsConstructor() {
    // Given
    List<FieldError> errors = new ArrayList<>();
    errors.add(FieldError.builder().field("username").message("用户名不能为空").build());

    // When
    ValidationError validationError = new ValidationError(errors);

    // Then
    assertNotNull(validationError);
    assertNotNull(validationError.getErrors());
    assertEquals(1, validationError.getErrors().size());
  }

  @Test
  void testFromSpringFieldErrors() {
    // Given
    List<FieldError> springFieldErrors = new ArrayList<>();
    springFieldErrors.add(new FieldError("user", "username", "用户名不能为空"));
    springFieldErrors.add(new FieldError("user", "email", "邮箱格式不正确"));

    // When
    ValidationError validationError = ValidationError.from(springFieldErrors);

    // Then
    assertNotNull(validationError);
    assertNotNull(validationError.getErrors());
    assertEquals(2, validationError.getErrors().size());
    assertEquals("username", validationError.getErrors().get(0).getField());
    assertEquals("用户名不能为空", validationError.getErrors().get(0).getMessage());
    assertEquals("email", validationError.getErrors().get(1).getField());
    assertEquals("邮箱格式不正确", validationError.getErrors().get(1).getMessage());
  }

  @Test
  void testFromSpringFieldErrorsWithNull() {
    // When
    ValidationError validationError = ValidationError.from(null);

    // Then
    assertNotNull(validationError);
    assertNotNull(validationError.getErrors());
    assertTrue(validationError.getErrors().isEmpty());
  }

  @Test
  void testFromSpringFieldErrorsWithEmptyList() {
    // Given
    List<FieldError> springFieldErrors = new ArrayList<>();

    // When
    ValidationError validationError = ValidationError.from(springFieldErrors);

    // Then
    assertNotNull(validationError);
    assertNotNull(validationError.getErrors());
    assertTrue(validationError.getErrors().isEmpty());
  }

  @Test
  void testFromConstraintViolations() {
    // Given
    TestObject testObject = new TestObject();
    testObject.setUsername("");
    testObject.setEmail(null);

    Set<ConstraintViolation<TestObject>> violations = validator.validate(testObject);

    // When
    ValidationError validationError =
        ValidationError.fromConstraintViolations(new ArrayList<>(violations));

    // Then
    assertNotNull(validationError);
    assertNotNull(validationError.getErrors());
    assertTrue(validationError.getErrors().size() >= 2);
  }

  @Test
  void testFromConstraintViolationsWithNull() {
    // When
    ValidationError validationError = ValidationError.fromConstraintViolations(null);

    // Then
    assertNotNull(validationError);
    assertNotNull(validationError.getErrors());
    assertTrue(validationError.getErrors().isEmpty());
  }

  @Test
  void testFromConstraintViolationsWithEmptyList() {
    // Given
    List<ConstraintViolation<?>> violations = new ArrayList<>();

    // When
    ValidationError validationError = ValidationError.fromConstraintViolations(violations);

    // Then
    assertNotNull(validationError);
    assertNotNull(validationError.getErrors());
    assertTrue(validationError.getErrors().isEmpty());
  }

  @Test
  void testJsonSerialization() throws Exception {
    // Given
    List<FieldError> errors = new ArrayList<>();
    errors.add(FieldError.builder().field("username").message("用户名不能为空").build());
    errors.add(FieldError.builder().field("email").message("邮箱格式不正确").build());
    ValidationError validationError = ValidationError.builder().errors(errors).build();

    // When
    String json = objectMapper.writeValueAsString(validationError);

    // Then
    assertNotNull(json);
    assertTrue(json.contains("\"errors\""));
    assertTrue(json.contains("\"username\""));
    assertTrue(json.contains("\"邮箱格式不正确\""));
  }

  @Test
  void testJsonDeserialization() throws Exception {
    // Given
    String json =
        "{\"errors\":[{\"field\":\"username\",\"message\":\"用户名不能为空\"},{\"field\":\"email\",\"message\":\"邮箱格式不正确\"}]}";

    // When
    ValidationError validationError = objectMapper.readValue(json, ValidationError.class);

    // Then
    assertNotNull(validationError);
    assertNotNull(validationError.getErrors());
    assertEquals(2, validationError.getErrors().size());
    assertEquals("username", validationError.getErrors().get(0).getField());
    assertEquals("邮箱格式不正确", validationError.getErrors().get(1).getMessage());
  }

  /** 测试对象，用于验证 ConstraintViolation */
  static class TestObject {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotNull(message = "邮箱不能为空")
    private String email;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }
}
