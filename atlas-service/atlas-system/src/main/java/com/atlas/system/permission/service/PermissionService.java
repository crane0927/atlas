/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.service;

import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import java.util.List;

/**
 * 权限服务接口
 *
 * <p>提供权限查询业务逻辑，支持查询用户角色、权限和完整权限信息。
 *
 * <p>方法说明：
 *
 * <ul>
 *   <li>getRolesByUserId：查询用户角色列表
 *   <li>getPermissionsByUserId：查询用户权限列表
 *   <li>getAuthoritiesByUserId：查询用户完整权限信息（角色+权限）
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface PermissionService {

  /**
   * 查询用户角色列表
   *
   * <p>通过用户ID查询用户拥有的角色代码列表。
   *
   * @param userId 用户ID
   * @return 角色代码列表，如果用户不存在或没有角色则返回空列表
   */
  List<String> getRolesByUserId(Long userId);

  /**
   * 查询用户权限列表
   *
   * <p>通过用户ID查询用户拥有的权限代码列表（通过角色关联查询并去重）。
   *
   * @param userId 用户ID
   * @return 权限代码列表，如果用户不存在或没有权限则返回空列表
   */
  List<String> getPermissionsByUserId(Long userId);

  /**
   * 查询用户完整权限信息（角色+权限）
   *
   * <p>通过用户ID查询用户的完整权限信息，包括角色列表和权限列表。
   *
   * @param userId 用户ID
   * @return 用户权限信息 DTO，如果用户不存在则返回空列表的角色和权限
   */
  UserAuthoritiesDTO getAuthoritiesByUserId(Long userId);
}
