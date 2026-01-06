/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.api.v1.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * UserAuthoritiesDTO 单元测试
 *
 * <p>测试 UserAuthoritiesDTO 的序列化/反序列化功能和向后兼容性。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class UserAuthoritiesDTOTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testUserAuthoritiesDTOCreation() {
    // 测试创建 UserAuthoritiesDTO 对象
    UserAuthoritiesDTO dto = new UserAuthoritiesDTO();
    assertNotNull(dto);
  }

  @Test
  void testUserAuthoritiesDTOSettersAndGetters() {
    // 测试字段的 setter 和 getter
    UserAuthoritiesDTO dto = new UserAuthoritiesDTO();
    dto.setUserId(1L);
    dto.setRoles(Arrays.asList("ADMIN", "USER"));
    dto.setPermissions(Arrays.asList("user:read", "user:write"));

    assertEquals(1L, dto.getUserId());
    assertEquals(2, dto.getRoles().size());
    assertTrue(dto.getRoles().contains("ADMIN"));
    assertTrue(dto.getRoles().contains("USER"));
    assertEquals(2, dto.getPermissions().size());
    assertTrue(dto.getPermissions().contains("user:read"));
    assertTrue(dto.getPermissions().contains("user:write"));
  }

  @Test
  void testUserAuthoritiesDTOSerialization() throws JsonProcessingException {
    // 测试 UserAuthoritiesDTO 序列化为 JSON
    UserAuthoritiesDTO dto = new UserAuthoritiesDTO();
    dto.setUserId(1L);
    dto.setRoles(Arrays.asList("ADMIN", "USER"));
    dto.setPermissions(Arrays.asList("user:read", "user:write"));

    String json = objectMapper.writeValueAsString(dto);
    assertNotNull(json);
    // 验证 JSON 包含必要字段
    assertEquals(true, json.contains("\"userId\":1"));
    assertEquals(true, json.contains("\"roles\""));
    assertEquals(true, json.contains("\"permissions\""));
  }

  @Test
  void testUserAuthoritiesDTODeserialization() throws JsonProcessingException {
    // 测试从 JSON 反序列化为 UserAuthoritiesDTO
    String json =
        "{\"userId\":1,\"roles\":[\"ADMIN\",\"USER\"],\"permissions\":[\"user:read\",\"user:write\"]}";

    UserAuthoritiesDTO dto = objectMapper.readValue(json, UserAuthoritiesDTO.class);
    assertNotNull(dto);
    assertEquals(1L, dto.getUserId());
    assertEquals(2, dto.getRoles().size());
    assertTrue(dto.getRoles().contains("ADMIN"));
    assertTrue(dto.getRoles().contains("USER"));
    assertEquals(2, dto.getPermissions().size());
    assertTrue(dto.getPermissions().contains("user:read"));
    assertTrue(dto.getPermissions().contains("user:write"));
  }

  @Test
  void testUserAuthoritiesDTOEmptyLists() {
    // 测试空列表
    UserAuthoritiesDTO dto = new UserAuthoritiesDTO();
    dto.setUserId(1L);
    dto.setRoles(Collections.emptyList());
    dto.setPermissions(Collections.emptyList());

    assertEquals(1L, dto.getUserId());
    assertNotNull(dto.getRoles());
    assertTrue(dto.getRoles().isEmpty());
    assertNotNull(dto.getPermissions());
    assertTrue(dto.getPermissions().isEmpty());
  }

  @Test
  void testUserAuthoritiesDTOBackwardCompatibility() throws JsonProcessingException {
    // 测试向后兼容性：列表可以为空
    String json = "{\"userId\":1,\"roles\":[],\"permissions\":[]}";

    UserAuthoritiesDTO dto = objectMapper.readValue(json, UserAuthoritiesDTO.class);
    assertNotNull(dto);
    assertEquals(1L, dto.getUserId());
    assertNotNull(dto.getRoles());
    assertTrue(dto.getRoles().isEmpty());
    assertNotNull(dto.getPermissions());
    assertTrue(dto.getPermissions().isEmpty());
  }

  @Test
  void testUserAuthoritiesDTOAllArgsConstructor() {
    // 测试全参构造函数
    List<String> roles = Arrays.asList("ADMIN", "USER");
    List<String> permissions = Arrays.asList("user:read", "user:write");
    UserAuthoritiesDTO dto = new UserAuthoritiesDTO(1L, roles, permissions);

    assertNotNull(dto);
    assertEquals(1L, dto.getUserId());
    assertEquals(2, dto.getRoles().size());
    assertTrue(dto.getRoles().contains("ADMIN"));
    assertTrue(dto.getRoles().contains("USER"));
    assertEquals(2, dto.getPermissions().size());
    assertTrue(dto.getPermissions().contains("user:read"));
    assertTrue(dto.getPermissions().contains("user:write"));
  }
}

