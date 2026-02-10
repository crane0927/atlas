/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.role.controller;

import com.atlas.common.feature.core.page.PageResult;
import com.atlas.common.feature.core.result.Result;
import com.atlas.system.role.model.dto.RoleCreateDTO;
import com.atlas.system.role.model.dto.RoleQueryDTO;
import com.atlas.system.role.model.dto.RoleUpdateDTO;
import com.atlas.system.role.model.vo.RoleListVO;
import com.atlas.system.role.service.RoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
 *   <li>GET /api/v1/roles：分页查询角色列表（参数：page、size、sort、roleCode、roleName、status）
 *   <li>GET /api/v1/roles/{roleId}：角色详情
 *   <li>POST /api/v1/roles：创建角色
 *   <li>PUT /api/v1/roles/{roleId}：更新角色
 *   <li>DELETE /api/v1/roles/{roleId}：逻辑删除角色
 *   <li>POST /api/v1/roles/{roleId}/permissions：为角色分配权限
 *   <li>DELETE /api/v1/roles/{roleId}/permissions/{permissionId}：移除角色权限关联
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
   * 分页查询角色列表
   *
   * <p>支持按角色代码、角色名称、状态筛选，以及排序（排序字段：roleCode、roleName、createdAt，兼容 createTime/createdAt）。{@link RoleQueryDTO} 继承
   * PageQueryDTO，含 page、size、sort。
   *
   * @param query 查询条件（roleCode、roleName、status、page、size、sort）
   * @return 分页结果，使用 {@link Result} 包装
   */
  @GetMapping("/roles")
  public Result<PageResult<RoleListVO>> listRoles(RoleQueryDTO query) {
    PageResult<RoleListVO> result = roleService.listRolesPage(query);
    return Result.success(result);
  }

  /**
   * 根据角色ID查询角色详情
   *
   * @param roleId 角色ID
   * @return 角色详情，使用 {@link Result} 包装
   */
  @GetMapping("/roles/{roleId}")
  public Result<RoleListVO> getRoleById(@PathVariable String roleId) {
    RoleListVO vo = roleService.getRoleById(roleId);
    return Result.success(vo);
  }

  /**
   * 创建角色
   *
   * <p>根据角色创建 DTO 创建新角色。
   *
   * @param roleCreateDTO 角色创建 DTO
   * @return 角色ID，使用 {@link Result} 包装
   */
  @PostMapping("/roles")
  public Result<String> createRole(@Valid @RequestBody RoleCreateDTO roleCreateDTO) {
    String roleId = roleService.createRole(roleCreateDTO);
    return Result.success(roleId);
  }

  /**
   * 更新角色
   *
   * @param roleId 角色ID
   * @param roleUpdateDTO 更新 DTO
   * @return 操作结果
   */
  @PutMapping("/roles/{roleId}")
  public Result<Void> updateRole(
      @PathVariable String roleId, @Valid @RequestBody RoleUpdateDTO roleUpdateDTO) {
    roleService.updateRole(roleId, roleUpdateDTO);
    return Result.success(null);
  }

  /**
   * 逻辑删除角色
   *
   * @param roleId 角色ID
   * @return 操作结果
   */
  @DeleteMapping("/roles/{roleId}")
  public Result<Void> deleteRole(@PathVariable String roleId) {
    roleService.deleteRole(roleId);
    return Result.success(null);
  }

  /**
   * 为角色分配权限
   *
   * <p>建立角色与权限的关联关系。若该角色已拥有该权限（关联已存在），则视为成功，不报错（幂等）。
   *
   * @param roleId 角色ID
   * @param request 分配权限请求对象，含 permissionId
   * @return 操作结果，使用 {@link Result} 包装
   */
  @PostMapping("/roles/{roleId}/permissions")
  public Result<Void> assignPermissionToRole(
      @PathVariable String roleId, @Valid @RequestBody AssignPermissionRequest request) {
    roleService.assignPermissionToRole(roleId, request.getPermissionId());
    return Result.success(null);
  }

  /**
   * 移除角色与权限的关联
   *
   * @param roleId 角色ID
   * @param permissionId 权限ID
   * @return 操作结果
   */
  @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
  public Result<Void> removePermissionFromRole(
      @PathVariable String roleId, @PathVariable String permissionId) {
    roleService.removePermissionFromRole(roleId, permissionId);
    return Result.success(null);
  }

  /** 分配权限请求对象 */
  public static class AssignPermissionRequest {
    @NotNull(message = "权限ID不能为空")
    private String permissionId;

    public String getPermissionId() {
      return permissionId;
    }

    public void setPermissionId(String permissionId) {
      this.permissionId = permissionId;
    }
  }
}
