/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.result;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/** Result 类单元测试 */
class ResultTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    MDC.clear();
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void testSuccessWithData() {
    // Given
    String testData = "test data";

    // When
    Result<String> result = Result.success(testData);

    // Then
    assertNotNull(result);
    assertEquals("000000", result.getCode());
    assertEquals("操作成功", result.getMessage());
    assertEquals(testData, result.getData());
    assertNotNull(result.getTimestamp());
    assertTrue(result.getTimestamp() > 0);
    assertTrue(result.isSuccess());
  }

  @Test
  void testSuccessWithNullData() {
    // When
    Result<String> result = Result.success((String) null);

    // Then
    assertNotNull(result);
    assertEquals("000000", result.getCode());
    assertEquals("操作成功", result.getMessage());
    assertNull(result.getData());
    assertTrue(result.isSuccess());
  }

  @Test
  void testSuccessWithCustomMessage() {
    // Given
    String customMessage = "自定义成功消息";
    String testData = "test data";

    // When
    Result<String> result = Result.success(customMessage, testData);

    // Then
    assertNotNull(result);
    assertEquals("000000", result.getCode());
    assertEquals(customMessage, result.getMessage());
    assertEquals(testData, result.getData());
    assertTrue(result.isSuccess());
  }

  @Test
  void testErrorWithCodeAndMessage() {
    // Given
    String errorCode = "050000";
    String errorMessage = "系统错误";

    // When
    Result<String> result = Result.error(errorCode, errorMessage);

    // Then
    assertNotNull(result);
    assertEquals(errorCode, result.getCode());
    assertEquals(errorMessage, result.getMessage());
    assertNull(result.getData());
    assertFalse(result.isSuccess());
  }

  @Test
  void testErrorWithCodeMessageAndData() {
    // Given
    String errorCode = "050000";
    String errorMessage = "系统错误";
    String errorData = "错误详情";

    // When
    Result<String> result = Result.error(errorCode, errorMessage, errorData);

    // Then
    assertNotNull(result);
    assertEquals(errorCode, result.getCode());
    assertEquals(errorMessage, result.getMessage());
    assertEquals(errorData, result.getData());
    assertFalse(result.isSuccess());
  }

  @Test
  void testIsSuccess() {
    // Given & When
    Result<String> successResult = Result.success("data");
    Result<String> errorResult = Result.error("050000", "error");

    // Then
    assertTrue(successResult.isSuccess());
    assertFalse(errorResult.isSuccess());
  }

  @Test
  void testTraceIdFromMDC() {
    // Given
    String traceId = "test-trace-id-12345";
    MDC.put("traceId", traceId);

    // When
    Result<String> result = Result.success("data");

    // Then
    assertNotNull(result);
    assertEquals(traceId, result.getTraceId());
  }

  @Test
  void testTraceIdNullWhenMDCEmpty() {
    // Given - MDC is cleared in setUp

    // When
    Result<String> result = Result.success("data");

    // Then
    assertNotNull(result);
    assertNull(result.getTraceId());
  }

  @Test
  void testJsonSerialization() throws Exception {
    // Given
    String testData = "test data";
    Result<String> result = Result.success(testData);

    // When
    String json = objectMapper.writeValueAsString(result);

    // Then
    assertNotNull(json);
    assertTrue(json.contains("\"code\":\"000000\""));
    assertTrue(json.contains("\"message\":\"操作成功\""));
    assertTrue(json.contains("\"data\":\"test data\""));
    assertTrue(json.contains("\"timestamp\""));
  }

  @Test
  void testJsonSerializationWithNullData() throws Exception {
    // Given
    Result<String> result = Result.error("050000", "error");

    // When
    String json = objectMapper.writeValueAsString(result);

    // Then
    assertNotNull(json);
    assertTrue(json.contains("\"code\":\"050000\""));
    assertTrue(json.contains("\"message\":\"error\""));
    // data 为 null 时，由于 @JsonInclude(NON_NULL)，不应该包含在 JSON 中
    assertFalse(json.contains("\"data\":null"));
  }

  @Test
  void testJsonSerializationWithTraceId() throws Exception {
    // Given
    String traceId = "test-trace-id-12345";
    MDC.put("traceId", traceId);
    Result<String> result = Result.success("data");

    // When
    String json = objectMapper.writeValueAsString(result);

    // Then
    assertNotNull(json);
    assertTrue(json.contains("\"traceId\":\"test-trace-id-12345\""));
  }

  @Test
  void testJsonSerializationWithoutTraceId() throws Exception {
    // Given - MDC is cleared
    Result<String> result = Result.success("data");

    // When
    String json = objectMapper.writeValueAsString(result);

    // Then
    assertNotNull(json);
    // traceId 为 null 时，由于 @JsonInclude(NON_NULL)，不应该包含在 JSON 中
    assertFalse(json.contains("\"traceId\""));
  }

  @Test
  void testJsonDeserialization() throws Exception {
    // Given
    String json =
        "{\"code\":\"000000\",\"message\":\"操作成功\",\"data\":\"test\",\"timestamp\":1234567890}";

    // When
    Result<String> result = objectMapper.readValue(json, Result.class);

    // Then
    assertNotNull(result);
    assertEquals("000000", result.getCode());
    assertEquals("操作成功", result.getMessage());
    assertEquals("test", result.getData());
    assertEquals(1234567890L, result.getTimestamp());
  }
}
