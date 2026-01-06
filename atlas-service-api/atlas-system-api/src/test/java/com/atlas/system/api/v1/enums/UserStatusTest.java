/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.api.v1.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * UserStatus 枚举单元测试
 *
 * <p>测试 UserStatus 枚举的序列化/反序列化功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class UserStatusTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testEnumValues() {
    // 验证所有枚举值存在
    assertNotNull(UserStatus.ACTIVE);
    assertNotNull(UserStatus.INACTIVE);
    assertNotNull(UserStatus.LOCKED);
    assertNotNull(UserStatus.DELETED);
  }

  @Test
  void testEnumSerialization() throws JsonProcessingException {
    // 测试枚举序列化为字符串
    String json = objectMapper.writeValueAsString(UserStatus.ACTIVE);
    assertEquals("\"ACTIVE\"", json);

    json = objectMapper.writeValueAsString(UserStatus.INACTIVE);
    assertEquals("\"INACTIVE\"", json);

    json = objectMapper.writeValueAsString(UserStatus.LOCKED);
    assertEquals("\"LOCKED\"", json);

    json = objectMapper.writeValueAsString(UserStatus.DELETED);
    assertEquals("\"DELETED\"", json);
  }

  @Test
  void testEnumDeserialization() throws JsonProcessingException {
    // 测试从字符串反序列化为枚举
    UserStatus status = objectMapper.readValue("\"ACTIVE\"", UserStatus.class);
    assertEquals(UserStatus.ACTIVE, status);

    status = objectMapper.readValue("\"INACTIVE\"", UserStatus.class);
    assertEquals(UserStatus.INACTIVE, status);

    status = objectMapper.readValue("\"LOCKED\"", UserStatus.class);
    assertEquals(UserStatus.LOCKED, status);

    status = objectMapper.readValue("\"DELETED\"", UserStatus.class);
    assertEquals(UserStatus.DELETED, status);
  }

  @Test
  void testEnumName() {
    // 验证枚举名称
    assertEquals("ACTIVE", UserStatus.ACTIVE.name());
    assertEquals("INACTIVE", UserStatus.INACTIVE.name());
    assertEquals("LOCKED", UserStatus.LOCKED.name());
    assertEquals("DELETED", UserStatus.DELETED.name());
  }
}

