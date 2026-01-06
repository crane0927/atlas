/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.api.v1.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.atlas.system.api.v1.model.enums.UserStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/**
 * UserDTO 单元测试
 *
 * <p>测试 UserDTO 的序列化/反序列化功能和向后兼容性。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class UserDTOTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testUserDTOCreation() {
    // 测试创建 UserDTO 对象
    UserDTO userDTO = new UserDTO();
    assertNotNull(userDTO);
  }

  @Test
  void testUserDTOSettersAndGetters() {
    // 测试字段的 setter 和 getter
    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(1L);
    userDTO.setUsername("admin");
    userDTO.setNickname("管理员");
    userDTO.setEmail("admin@example.com");
    userDTO.setPhone("13800138000");
    userDTO.setStatus(UserStatus.ACTIVE);
    userDTO.setAvatar("https://example.com/avatar.jpg");

    assertEquals(1L, userDTO.getUserId());
    assertEquals("admin", userDTO.getUsername());
    assertEquals("管理员", userDTO.getNickname());
    assertEquals("admin@example.com", userDTO.getEmail());
    assertEquals("13800138000", userDTO.getPhone());
    assertEquals(UserStatus.ACTIVE, userDTO.getStatus());
    assertEquals("https://example.com/avatar.jpg", userDTO.getAvatar());
  }

  @Test
  void testUserDTOSerialization() throws JsonProcessingException {
    // 测试 UserDTO 序列化为 JSON
    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(1L);
    userDTO.setUsername("admin");
    userDTO.setNickname("管理员");
    userDTO.setEmail("admin@example.com");
    userDTO.setPhone("13800138000");
    userDTO.setStatus(UserStatus.ACTIVE);
    userDTO.setAvatar("https://example.com/avatar.jpg");

    String json = objectMapper.writeValueAsString(userDTO);
    assertNotNull(json);
    // 验证 JSON 包含必要字段
    assertEquals(true, json.contains("\"userId\":1"));
    assertEquals(true, json.contains("\"username\":\"admin\""));
    assertEquals(true, json.contains("\"status\":\"ACTIVE\""));
  }

  @Test
  void testUserDTODeserialization() throws JsonProcessingException {
    // 测试从 JSON 反序列化为 UserDTO
    String json =
        "{\"userId\":1,\"username\":\"admin\",\"nickname\":\"管理员\",\"email\":\"admin@example.com\",\"phone\":\"13800138000\",\"status\":\"ACTIVE\",\"avatar\":\"https://example.com/avatar.jpg\"}";

    UserDTO userDTO = objectMapper.readValue(json, UserDTO.class);
    assertNotNull(userDTO);
    assertEquals(1L, userDTO.getUserId());
    assertEquals("admin", userDTO.getUsername());
    assertEquals("管理员", userDTO.getNickname());
    assertEquals("admin@example.com", userDTO.getEmail());
    assertEquals("13800138000", userDTO.getPhone());
    assertEquals(UserStatus.ACTIVE, userDTO.getStatus());
    assertEquals("https://example.com/avatar.jpg", userDTO.getAvatar());
  }

  @Test
  void testUserDTOBackwardCompatibility() throws JsonProcessingException {
    // 测试向后兼容性：可选字段可以为 null
    String json = "{\"userId\":1,\"username\":\"admin\",\"status\":\"ACTIVE\"}";

    UserDTO userDTO = objectMapper.readValue(json, UserDTO.class);
    assertNotNull(userDTO);
    assertEquals(1L, userDTO.getUserId());
    assertEquals("admin", userDTO.getUsername());
    assertEquals(UserStatus.ACTIVE, userDTO.getStatus());
    // 可选字段可以为 null
    assertNull(userDTO.getNickname());
    assertNull(userDTO.getEmail());
    assertNull(userDTO.getPhone());
    assertNull(userDTO.getAvatar());
  }

  @Test
  void testUserDTOAllArgsConstructor() {
    // 测试全参构造函数
    UserDTO userDTO =
        new UserDTO(
            1L,
            "admin",
            "管理员",
            "admin@example.com",
            "13800138000",
            UserStatus.ACTIVE,
            "https://example.com/avatar.jpg");

    assertNotNull(userDTO);
    assertEquals(1L, userDTO.getUserId());
    assertEquals("admin", userDTO.getUsername());
    assertEquals("管理员", userDTO.getNickname());
    assertEquals("admin@example.com", userDTO.getEmail());
    assertEquals("13800138000", userDTO.getPhone());
    assertEquals(UserStatus.ACTIVE, userDTO.getStatus());
    assertEquals("https://example.com/avatar.jpg", userDTO.getAvatar());
  }
}

