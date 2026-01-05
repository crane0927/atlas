/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * LongToStringSerializer 单元测试
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class LongToStringSerializerTest {

  private ObjectMapper objectMapper;
  private LongToStringSerializer serializer;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    serializer = new LongToStringSerializer();

    // 注册序列化器
    SimpleModule module = new SimpleModule();
    module.addSerializer(Long.class, serializer);
    module.addSerializer(Long.TYPE, serializer);
    objectMapper.registerModule(module);
  }

  @Test
  void testSerializeLong() throws JsonProcessingException {
    // Given
    TestObject obj = new TestObject();
    obj.setId(1234567890123456789L);

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    assertEquals("{\"id\":\"1234567890123456789\"}", json);
  }

  @Test
  void testSerializeLongZero() throws JsonProcessingException {
    // Given
    TestObject obj = new TestObject();
    obj.setId(0L);

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    assertEquals("{\"id\":\"0\"}", json);
  }

  @Test
  void testSerializeLongNegative() throws JsonProcessingException {
    // Given
    TestObject obj = new TestObject();
    obj.setId(-1234567890123456789L);

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    assertEquals("{\"id\":\"-1234567890123456789\"}", json);
  }

  @Test
  void testSerializeLongMaxValue() throws JsonProcessingException {
    // Given
    TestObject obj = new TestObject();
    obj.setId(Long.MAX_VALUE);

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    assertEquals("{\"id\":\"9223372036854775807\"}", json);
  }

  @Test
  void testSerializeLongMinValue() throws JsonProcessingException {
    // Given
    TestObject obj = new TestObject();
    obj.setId(Long.MIN_VALUE);

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    assertEquals("{\"id\":\"-9223372036854775808\"}", json);
  }

  @Test
  void testSerializeLongNull() throws JsonProcessingException {
    // Given
    TestObject obj = new TestObject();
    obj.setId(null);

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    // null 值会被序列化为 null 或忽略（取决于 ObjectMapper 配置）
    assertTrue(json.contains("\"id\":null") || json.equals("{}"));
  }

  @Test
  void testSerializePrimitiveLong() throws JsonProcessingException {
    // Given
    TestObjectPrimitive obj = new TestObjectPrimitive();
    obj.setId(1234567890123456789L);

    // When
    String json = objectMapper.writeValueAsString(obj);

    // Then
    assertNotNull(json);
    assertEquals("{\"id\":\"1234567890123456789\"}", json);
  }

  /** 测试对象（Long 包装类型） */
  static class TestObject {
    private Long id;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }
  }

  /** 测试对象（long 原始类型） */
  static class TestObjectPrimitive {
    private long id;

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }
  }
}
