/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.api.v1.feign;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 权限查询接口
 *
 * <p>定义权限查询的 Feign 接口，供 auth 服务查询用户权限和角色信息。
 *
 * <p>接口说明：
 * <ul>
 *   <li>getUserRoles：查询用户角色列表</li>
 *   <li>getUserPermissions：查询用户权限列表</li>
 *   <li>getUserAuthorities：查询用户完整权限信息（角色+权限）</li>
 * </ul>
 *
 * <p>服务名称：atlas-system
 *
 * <p>基础路径：/api/v1
 *
 * <p>返回格式：统一使用 {@link Result} 包装响应数据
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@FeignClient(name = "atlas-system", path = "/api/v1")
public interface PermissionQueryApi {

  /**
   * 查询用户角色列表
   *
   * <p>通过用户ID查询用户拥有的角色列表。
   *
   * @param userId 用户ID
   * @return 角色列表，使用 {@link Result} 包装
   */
  @GetMapping("/users/{userId}/roles")
  Result<List<String>> getUserRoles(@PathVariable Long userId);

  /**
   * 查询用户权限列表
   *
   * <p>通过用户ID查询用户拥有的权限列表。
   *
   * @param userId 用户ID
   * @return 权限列表，使用 {@link Result} 包装
   */
  @GetMapping("/users/{userId}/permissions")
  Result<List<String>> getUserPermissions(@PathVariable Long userId);

  /**
   * 查询用户完整权限信息（角色+权限）
   *
   * <p>通过用户ID查询用户的完整权限信息，包括角色列表和权限列表。
   *
   * @param userId 用户ID
   * @return 用户权限信息，使用 {@link Result} 包装
   */
  @GetMapping("/users/{userId}/authorities")
  Result<UserAuthoritiesDTO> getUserAuthorities(@PathVariable Long userId);
}

