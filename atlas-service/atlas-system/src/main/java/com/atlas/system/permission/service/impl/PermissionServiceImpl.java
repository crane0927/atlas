/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.service.impl;

import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import com.atlas.system.permission.service.PermissionService;
import com.atlas.system.role.mapper.RolePermissionMapper;
import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.mapper.UserRoleMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 权限服务实现类
 *
 * <p>实现权限查询业务逻辑，包括查询用户角色、权限和完整权限信息。
 *
 * <p>功能说明：
 *
 * <ul>
 *   <li>实现 getRolesByUserId 方法，查询用户角色列表
 *   <li>实现 getPermissionsByUserId 方法，通过角色关联查询权限列表并去重
 *   <li>实现 getAuthoritiesByUserId 方法，返回 UserAuthoritiesDTO
 *   <li>处理用户不存在的情况，返回空列表
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final UserMapper userMapper;
  private final UserRoleMapper userRoleMapper;
  private final RolePermissionMapper rolePermissionMapper;

  /**
   * 查询用户角色列表
   *
   * @param userId 用户ID
   * @return 角色代码列表，如果用户不存在或没有角色则返回空列表
   */
  @Override
  public List<String> getRolesByUserId(Long userId) {
    // 检查用户是否存在
    if (userMapper.selectById(userId) == null) {
      return Collections.emptyList();
    }
    // 查询用户角色代码列表
    List<String> roleCodes = userRoleMapper.selectRoleCodesByUserId(userId);
    return roleCodes != null ? roleCodes : Collections.emptyList();
  }

  /**
   * 查询用户权限列表
   *
   * @param userId 用户ID
   * @return 权限代码列表，如果用户不存在或没有权限则返回空列表
   */
  @Override
  public List<String> getPermissionsByUserId(Long userId) {
    // 检查用户是否存在
    if (userMapper.selectById(userId) == null) {
      return Collections.emptyList();
    }
    // 查询用户角色ID列表
    List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
    if (roleIds == null || roleIds.isEmpty()) {
      return Collections.emptyList();
    }
    // 根据角色ID列表查询权限代码列表（已去重）
    List<String> permissionCodes = rolePermissionMapper.selectPermissionCodesByRoleIds(roleIds);
    return permissionCodes != null ? permissionCodes : Collections.emptyList();
  }

  /**
   * 查询用户完整权限信息（角色+权限）
   *
   * @param userId 用户ID
   * @return 用户权限信息 DTO
   */
  @Override
  public UserAuthoritiesDTO getAuthoritiesByUserId(Long userId) {
    List<String> roles = getRolesByUserId(userId);
    List<String> permissions = getPermissionsByUserId(userId);
    UserAuthoritiesDTO dto = new UserAuthoritiesDTO();
    dto.setUserId(userId);
    dto.setRoles(roles != null ? roles : new ArrayList<>());
    dto.setPermissions(permissions != null ? permissions : new ArrayList<>());
    return dto;
  }
}
