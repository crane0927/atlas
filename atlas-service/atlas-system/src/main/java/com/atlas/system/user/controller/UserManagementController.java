/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.controller;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.user.model.dto.UserCreateDTO;
import com.atlas.system.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
 *   <li>POST /api/v1/users/{userId}/roles：为用户分配角色
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
   * 为用户分配角色
   *
   * <p>建立用户与角色的关联关系。若该用户已拥有该角色（关联已存在），则视为成功，不报错（幂等）。
   *
   * @param userId 用户ID
   * @param request 请求体，含 roleId
   * @return 操作结果，使用 {@link Result} 包装
   */
  @PostMapping("/users/{userId}/roles")
  public Result<Void> assignRoleToUser(
      @PathVariable String userId, @Valid @RequestBody AssignRoleRequest request) {
    userService.assignRoleToUser(userId, request.getRoleId());
    return Result.success(null);
  }

  /** 分配角色请求对象 */
  public static class AssignRoleRequest {
    @NotNull(message = "角色ID不能为空")
    private String roleId;

    public String getRoleId() {
      return roleId;
    }

    public void setRoleId(String roleId) {
      this.roleId = roleId;
    }
  }
}
