/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.controller;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.feign.PermissionQueryApi;
import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import com.atlas.system.permission.service.PermissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限查询控制器
 *
 * <p>实现 PermissionQueryApi 接口，提供权限查询的 RESTful API 接口。
 *
 * <p>接口说明：
 *
 * <ul>
 *   <li>GET /api/v1/users/{userId}/roles：查询用户角色列表
 *   <li>GET /api/v1/users/{userId}/permissions：查询用户权限列表
 *   <li>GET /api/v1/users/{userId}/authorities：查询用户完整权限信息（角色+权限）
 * </ul>
 *
 * <p>返回格式：统一使用 {@link Result} 包装响应数据
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class PermissionController implements PermissionQueryApi {

  private final PermissionService permissionService;

  /**
   * 查询用户角色列表
   *
   * <p>通过用户ID查询用户拥有的角色列表。
   *
   * @param userId 用户ID
   * @return 角色列表，使用 {@link Result} 包装
   */
  @Override
  @GetMapping("/api/v1/users/{userId}/roles")
  public Result<List<String>> getUserRoles(@PathVariable Long userId) {
    List<String> roles = permissionService.getRolesByUserId(userId);
    return Result.success(roles);
  }

  /**
   * 查询用户权限列表
   *
   * <p>通过用户ID查询用户拥有的权限列表。
   *
   * @param userId 用户ID
   * @return 权限列表，使用 {@link Result} 包装
   */
  @Override
  @GetMapping("/api/v1/users/{userId}/permissions")
  public Result<List<String>> getUserPermissions(@PathVariable Long userId) {
    List<String> permissions = permissionService.getPermissionsByUserId(userId);
    return Result.success(permissions);
  }

  /**
   * 查询用户完整权限信息（角色+权限）
   *
   * <p>通过用户ID查询用户的完整权限信息，包括角色列表和权限列表。
   *
   * @param userId 用户ID
   * @return 用户权限信息，使用 {@link Result} 包装
   */
  @Override
  @GetMapping("/api/v1/users/{userId}/authorities")
  public Result<UserAuthoritiesDTO> getUserAuthorities(@PathVariable Long userId) {
    UserAuthoritiesDTO authorities = permissionService.getAuthoritiesByUserId(userId);
    return Result.success(authorities);
  }
}
