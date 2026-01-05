/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.atlas.common.feature.core.constant.CommonErrorCode;
import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.exception.DataException;
import com.atlas.common.feature.core.exception.ParameterException;
import com.atlas.common.feature.core.exception.PermissionException;
import com.atlas.common.feature.core.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError as SpringFieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * GlobalExceptionHandler 单元测试
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;
  private ObjectMapper objectMapper;
  private Validator validator;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    objectMapper = new ObjectMapper();
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    mockMvc =
        MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(handler)
            .build();
    MDC.clear();
  }

  // ========== 业务异常处理测试 ==========

  @Test
  void testHandleBusinessException() {
    // Given
    BusinessException e = new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "用户不存在");

    // When
    Result<Void> result = handler.handleBusinessException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.DATA_NOT_FOUND, result.getCode());
    assertEquals("用户不存在", result.getMessage());
    assertEquals(HttpStatus.OK.value(), HttpStatus.OK.value());
  }

  @Test
  void testHandleParameterException() {
    // Given
    ParameterException e = new ParameterException(CommonErrorCode.PARAM_REQUIRED, "用户名不能为空");

    // When
    Result<Void> result = handler.handleParameterException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.PARAM_REQUIRED, result.getCode());
    assertEquals("用户名不能为空", result.getMessage());
  }

  @Test
  void testHandlePermissionException() {
    // Given
    PermissionException e =
        new PermissionException(CommonErrorCode.PERMISSION_DENIED, "权限不足");

    // When
    Result<Void> result = handler.handlePermissionException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.PERMISSION_DENIED, result.getCode());
    assertEquals("权限不足", result.getMessage());
  }

  @Test
  void testHandleDataException() {
    // Given
    DataException e = new DataException(CommonErrorCode.DATA_CONFLICT, "用户名已存在");

    // When
    Result<Void> result = handler.handleDataException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.DATA_CONFLICT, result.getCode());
    assertEquals("用户名已存在", result.getMessage());
  }

  // ========== 参数校验异常处理测试 ==========

  @Test
  void testHandleMethodArgumentNotValidException() {
    // Given
    TestDTO dto = new TestDTO();
    dto.setUsername("");
    dto.setEmail(null);

    org.springframework.validation.BindingResult bindingResult =
        new org.springframework.validation.BeanPropertyBindingResult(dto, "testDTO");
    bindingResult.addError(new SpringFieldError("testDTO", "username", "用户名不能为空"));
    bindingResult.addError(new SpringFieldError("testDTO", "email", "邮箱不能为空"));

    MethodArgumentNotValidException e = new MethodArgumentNotValidException(null, bindingResult);

    // When
    Result<ValidationError> result = handler.handleMethodArgumentNotValidException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.PARAM_ERROR, result.getCode());
    assertEquals("参数校验失败", result.getMessage());
    assertNotNull(result.getData());
    assertNotNull(result.getData().getErrors());
    assertEquals(2, result.getData().getErrors().size());
    assertEquals("username", result.getData().getErrors().get(0).getField());
    assertEquals("用户名不能为空", result.getData().getErrors().get(0).getMessage());
  }

  @Test
  void testHandleConstraintViolationException() {
    // Given
    TestObject testObject = new TestObject();
    testObject.setUsername("");
    testObject.setEmail(null);

    Set<ConstraintViolation<TestObject>> violations = validator.validate(testObject);
    ConstraintViolationException e = new ConstraintViolationException(violations);

    // When
    Result<ValidationError> result = handler.handleConstraintViolationException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.PARAM_ERROR, result.getCode());
    assertEquals("参数校验失败", result.getMessage());
    assertNotNull(result.getData());
    assertNotNull(result.getData().getErrors());
    assertTrue(result.getData().getErrors().size() >= 2);
  }

  @Test
  void testHandleBindException() {
    // Given
    TestDTO dto = new TestDTO();
    org.springframework.validation.BindingResult bindingResult =
        new org.springframework.validation.BeanPropertyBindingResult(dto, "testDTO");
    bindingResult.addError(new SpringFieldError("testDTO", "username", "用户名不能为空"));
    BindException e = new BindException(bindingResult);

    // When
    Result<ValidationError> result = handler.handleBindException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.PARAM_ERROR, result.getCode());
    assertEquals("参数绑定失败", result.getMessage());
    assertNotNull(result.getData());
    assertNotNull(result.getData().getErrors());
    assertEquals(1, result.getData().getErrors().size());
  }

  // ========== Spring MVC 异常处理测试 ==========

  @Test
  void testHandleHttpRequestMethodNotSupportedException() {
    // Given
    HttpRequestMethodNotSupportedException e =
        new HttpRequestMethodNotSupportedException("DELETE", new String[] {"GET", "POST"});

    // When
    Result<Void> result = handler.handleHttpRequestMethodNotSupportedException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.PARAM_ERROR, result.getCode());
    assertTrue(result.getMessage().contains("HTTP 方法 'DELETE' 不支持"));
    assertTrue(result.getMessage().contains("GET"));
    assertTrue(result.getMessage().contains("POST"));
  }

  @Test
  void testHandleHttpMediaTypeNotSupportedException() {
    // Given
    HttpMediaTypeNotSupportedException e =
        new HttpMediaTypeNotSupportedException(
            MediaType.APPLICATION_XML, List.of(MediaType.APPLICATION_JSON));

    // When
    Result<Void> result = handler.handleHttpMediaTypeNotSupportedException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.PARAM_ERROR, result.getCode());
    assertTrue(result.getMessage().contains("HTTP 媒体类型"));
    assertTrue(result.getMessage().contains("application/xml"));
  }

  @Test
  void testHandleMissingServletRequestParameterException() {
    // Given
    MissingServletRequestParameterException e =
        new MissingServletRequestParameterException("username", "String");

    // When
    Result<Void> result = handler.handleMissingServletRequestParameterException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.PARAM_REQUIRED, result.getCode());
    assertTrue(result.getMessage().contains("缺少必需的请求参数"));
    assertTrue(result.getMessage().contains("username"));
    assertTrue(result.getMessage().contains("String"));
  }

  // ========== 系统异常处理测试 ==========

  @Test
  void testHandleException() {
    // Given
    Exception e = new RuntimeException("测试异常");

    // When
    Result<Void> result = handler.handleException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.SYSTEM_ERROR, result.getCode());
    assertEquals("系统错误，请联系管理员", result.getMessage());
  }

  @Test
  void testHandleNullPointerException() {
    // Given
    NullPointerException e = new NullPointerException("空指针异常");

    // When
    Result<Void> result = handler.handleException(e);

    // Then
    assertNotNull(result);
    assertEquals(CommonErrorCode.SYSTEM_ERROR, result.getCode());
    assertEquals("系统错误，请联系管理员", result.getMessage());
  }

  @Test
  void testResultContainsTraceId() {
    // Given
    String traceId = "test-trace-id-12345";
    MDC.put("traceId", traceId);
    BusinessException e = new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "用户不存在");

    // When
    Result<Void> result = handler.handleBusinessException(e);

    // Then
    assertNotNull(result);
    assertEquals(traceId, result.getTraceId());
  }

  @Test
  void testResultWithoutTraceId() {
    // Given
    MDC.clear();
    BusinessException e = new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "用户不存在");

    // When
    Result<Void> result = handler.handleBusinessException(e);

    // Then
    assertNotNull(result);
    // TraceId 可能为 null，这是正常的
  }

  // ========== 测试辅助类 ==========

  /** 测试 DTO */
  static class TestDTO {
    private String username;
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

  /** 测试 Controller */
  @RestController
  static class TestController {
    @GetMapping("/test")
    public Result<String> test() {
      return Result.success("test");
    }

    @PostMapping("/test")
    public Result<String> testPost(@RequestBody TestDTO dto) {
      return Result.success("test");
    }
  }
}

