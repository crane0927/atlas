/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.controller;

import com.atlas.common.feature.core.page.PageResult;
import com.atlas.common.feature.core.result.Result;
import com.atlas.system.permission.model.dto.PermissionCreateDTO;
import com.atlas.system.permission.model.dto.PermissionQueryDTO;
import com.atlas.system.permission.model.dto.PermissionUpdateDTO;
import com.atlas.system.permission.model.vo.PermissionListVO;
import com.atlas.system.permission.service.PermissionService;
import jakarta.validation.Valid;
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
 * 权限管理控制器
 *
 * <p>提供权限管理的 RESTful API 接口，包括创建权限。
 *
 * <p>接口说明：
 *
 * <ul>
 *   <li>GET /api/v1/permissions：分页查询权限列表（参数：page、size、sort、permissionCode、permissionName、status）
 *   <li>GET /api/v1/permissions/{permissionId}：权限详情
 *   <li>POST /api/v1/permissions：创建权限
 *   <li>PUT /api/v1/permissions/{permissionId}：更新权限
 *   <li>DELETE /api/v1/permissions/{permissionId}：逻辑删除权限
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
public class PermissionManagementController {

  private final PermissionService permissionService;

  /**
   * 分页查询权限列表
   *
   * <p>支持按权限代码、权限名称、状态筛选，以及排序（排序字段：permissionCode、permissionName、createdAt，兼容 createTime/createdAt）。{@link
   * PermissionQueryDTO} 继承 PageQueryDTO，含 page、size、sort。
   *
   * @param query 查询条件（permissionCode、permissionName、status、page、size、sort）
   * @return 分页结果，使用 {@link Result} 包装
   */
  @GetMapping("/permissions")
  public Result<PageResult<PermissionListVO>> listPermissions(PermissionQueryDTO query) {
    PageResult<PermissionListVO> result = permissionService.listPermissionsPage(query);
    return Result.success(result);
  }

  /**
   * 根据权限ID查询权限详情
   *
   * @param permissionId 权限ID
   * @return 权限详情，使用 {@link Result} 包装
   */
  @GetMapping("/permissions/{permissionId}")
  public Result<PermissionListVO> getPermissionById(@PathVariable String permissionId) {
    PermissionListVO vo = permissionService.getPermissionById(permissionId);
    return Result.success(vo);
  }

  /**
   * 创建权限
   *
   * <p>根据权限创建 DTO 创建新权限。
   *
   * @param permissionCreateDTO 权限创建 DTO
   * @return 权限ID，使用 {@link Result} 包装
   */
  @PostMapping("/permissions")
  public Result<String> createPermission(
      @Valid @RequestBody PermissionCreateDTO permissionCreateDTO) {
    String permissionId = permissionService.createPermission(permissionCreateDTO);
    return Result.success(permissionId);
  }

  /**
   * 更新权限
   *
   * @param permissionId 权限ID
   * @param permissionUpdateDTO 更新 DTO
   * @return 操作结果
   */
  @PutMapping("/permissions/{permissionId}")
  public Result<Void> updatePermission(
      @PathVariable String permissionId,
      @Valid @RequestBody PermissionUpdateDTO permissionUpdateDTO) {
    permissionService.updatePermission(permissionId, permissionUpdateDTO);
    return Result.success(null);
  }

  /**
   * 逻辑删除权限
   *
   * @param permissionId 权限ID
   * @return 操作结果
   */
  @DeleteMapping("/permissions/{permissionId}")
  public Result<Void> deletePermission(@PathVariable String permissionId) {
    permissionService.deletePermission(permissionId);
    return Result.success(null);
  }
}
