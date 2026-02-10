/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.controller;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.user.model.dto.UserCreateDTO;
import com.atlas.system.user.model.dto.UserUpdateDTO;
import com.atlas.system.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器
 *
 * <p>提供用户管理的 RESTful API 接口，包括创建用户和分配角色。
 *
 * <p>接口说明：
 *
 * <ul>
 *   <li>POST /api/v1/users：创建用户
 *   <li>PUT /api/v1/users/{userId}：更新用户
 *   <li>DELETE /api/v1/users/{userId}：逻辑删除用户
 *   <li>POST /api/v1/users/{userId}/roles：为用户批量分配角色（请求体 roleIds）
 *   <li>DELETE /api/v1/users/{userId}/roles：批量移除用户角色关联（请求体 roleIds）
 * </ul>
 *
 * <p>返回格式：统一使用 {@link Result} 包装响应数据
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserManagementController {

  private final UserService userService;

  /**
   * 创建用户
   *
   * <p>根据用户创建 DTO 创建新用户，密码会自动加密存储。
   *
   * @param userCreateDTO 用户创建 DTO
   * @return 用户信息，使用 {@link Result} 包装
   */
  @PostMapping("/users")
  public Result<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
    UserDTO userDTO = userService.createUser(userCreateDTO);
    return Result.success(userDTO);
  }

  /**
   * 更新用户
   *
   * @param userId 用户ID
   * @param userUpdateDTO 更新 DTO
   * @return 更新后的用户信息
   */
  @PutMapping("/users/{userId}")
  public Result<UserDTO> updateUser(
      @PathVariable String userId, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
    UserDTO userDTO = userService.updateUser(userId, userUpdateDTO);
    return Result.success(userDTO);
  }

  /**
   * 逻辑删除用户
   *
   * @param userId 用户ID
   * @return 操作结果
   */
  @DeleteMapping("/users/{userId}")
  public Result<Void> deleteUser(@PathVariable String userId) {
    userService.deleteUser(userId);
    return Result.success(null);
  }

  /**
   * 为用户批量分配角色
   *
   * <p>建立用户与角色的关联关系。若某条关联已存在则跳过（幂等）。请求体 roleIds 为空时不执行任何操作。
   *
   * @param userId 用户ID
   * @param request 请求体，含 roleIds（角色ID列表）
   * @return 操作结果，使用 {@link Result} 包装
   */
  @PostMapping("/users/{userId}/roles")
  public Result<Void> assignRolesToUser(
      @PathVariable String userId, @Valid @RequestBody AssignRolesRequest request) {
    userService.assignRolesToUser(userId, request.getRoleIds());
    return Result.success(null);
  }

  /**
   * 批量移除用户与角色的关联
   *
   * @param userId 用户ID
   * @param request 请求体，含 roleIds（角色ID列表）
   * @return 操作结果
   */
  @DeleteMapping("/users/{userId}/roles")
  public Result<Void> removeRolesFromUser(
      @PathVariable String userId, @Valid @RequestBody RemoveRolesRequest request) {
    userService.removeRolesFromUser(userId, request.getRoleIds());
    return Result.success(null);
  }

  /** 批量分配角色请求对象 */
  @Data
  public static class AssignRolesRequest {
    /** 角色ID列表，可为空 */
    private List<String> roleIds;
  }

  /** 批量移除角色请求对象 */
  @Data
  public static class RemoveRolesRequest {
    /** 角色ID列表，可为空 */
    private List<String> roleIds;
  }
}
