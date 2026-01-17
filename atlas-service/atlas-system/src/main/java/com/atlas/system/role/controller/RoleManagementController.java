/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.role.controller;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.role.model.dto.RoleCreateDTO;
import com.atlas.system.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理控制器
 *
 * <p>提供角色管理的 RESTful API 接口，包括创建角色和分配权限。
 *
 * <p>接口说明：
 *
 * <ul>
 *   <li>POST /api/v1/roles：创建角色
 *   <li>POST /api/v1/roles/{roleId}/permissions：为角色分配权限
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
public class RoleManagementController {

  private final RoleService roleService;

  /**
   * 创建角色
   *
   * <p>根据角色创建 DTO 创建新角色。
   *
   * @param roleCreateDTO 角色创建 DTO
   * @return 角色ID，使用 {@link Result} 包装
   */
  @PostMapping("/roles")
  public Result<Long> createRole(@Valid @RequestBody RoleCreateDTO roleCreateDTO) {
    Long roleId = roleService.createRole(roleCreateDTO);
    return Result.success(roleId);
  }

  /**
   * 为角色分配权限
   *
   * <p>建立角色与权限的关联关系。
   *
   * @param roleId 角色ID
   * @param request 分配权限请求对象
   * @return 操作结果，使用 {@link Result} 包装
   */
  @PostMapping("/roles/{roleId}/permissions")
  public Result<Void> assignPermissionToRole(
      @PathVariable Long roleId, @RequestBody AssignPermissionRequest request) {
    roleService.assignPermissionToRole(roleId, request.getPermissionId());
    return Result.success(null);
  }

  /** 分配权限请求对象 */
  public static class AssignPermissionRequest {
    private Long permissionId;

    public Long getPermissionId() {
      return permissionId;
    }

    public void setPermissionId(Long permissionId) {
      this.permissionId = permissionId;
    }
  }
}
