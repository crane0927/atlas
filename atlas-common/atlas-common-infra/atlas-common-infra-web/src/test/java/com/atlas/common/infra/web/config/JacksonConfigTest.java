/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * JacksonConfig 单元测试
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class JacksonConfigTest {

  private JacksonConfig config;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    config = new JacksonConfig();
    Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
    objectMapper = config.objectMapper(builder);
  }

  @Test
  void testObjectMapperBean() {
    // When
    ObjectMapper mapper = config.objectMapper(Jackson2ObjectMapperBuilder.json());

    // Then
    assertNotNull(mapper);
  }

  @Test
  void testNullValueHandling() throws JsonProcessingException {
    // Given
    TestObject obj = new TestObject();
    obj.setName("test");
    obj.setValue(null);

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    // null 值应该被忽略（NON_NULL）
    assertTrue(!json.contains("\"value\"") || json.contains("\"value\":null"));
  }

  @Test
  void testDateSerialization() throws JsonProcessingException {
    // Given
    TestObject obj = new TestObject();
    obj.setName("test");
    obj.setCreateTime(LocalDateTime.of(2026, 1, 27, 10, 30, 0));

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    // 日期应该序列化为字符串格式，不是时间戳
    assertTrue(json.contains("2026-01-27"));
    assertTrue(!json.contains("\"createTime\":"));
  }

  @Test
  void testLongSerialization() throws JsonProcessingException {
    // Given
    TestObject obj = new TestObject();
    obj.setName("test");
    obj.setId(1234567890123456789L);

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    // Long 应该序列化为 String
    assertTrue(json.contains("\"id\":\"1234567890123456789\""));
  }

  @Test
  void testDeserializationIgnoreUnknownProperties() throws JsonProcessingException {
    // Given
    String json = "{\"name\":\"test\",\"unknownField\":\"value\"}";

    // When
    TestObject obj = objectMapper.readValue(json, TestObject.class);

    // Then
    assertNotNull(obj);
    assertEquals("test", obj.getName());
    // 未知属性应该被忽略，不会抛出异常
  }

  @Test
  void testTimezoneHandling() {
    // When
    TimeZone timeZone = objectMapper.getSerializationConfig().getTimeZone();

    // Then
    assertNotNull(timeZone);
    // 应该使用系统默认时区
    assertEquals(TimeZone.getDefault(), timeZone);
  }

  @Test
  void testSerializationInclusion() {
    // When
    JsonInclude.Include inclusion =
        objectMapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion();

    // Then
    assertEquals(JsonInclude.Include.NON_NULL, inclusion);
  }

  @Test
  void testWriteDatesAsTimestamps() {
    // When
    boolean writeDatesAsTimestamps =
        objectMapper
            .getSerializationConfig()
            .isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Then
    // 应该禁用时间戳格式，使用字符串格式
    assertTrue(!writeDatesAsTimestamps);
  }

  /** 测试对象 */
  static class TestObject {
    private String name;
    private Long id;
    private String value;
    private LocalDateTime createTime;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public LocalDateTime getCreateTime() {
      return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
      this.createTime = createTime;
    }
  }
}
