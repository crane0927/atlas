/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.role.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.permission.mapper.PermissionMapper;
import com.atlas.system.permission.model.entity.Permission;
import com.atlas.system.role.mapper.RoleMapper;
import com.atlas.system.role.mapper.RolePermissionMapper;
import com.atlas.system.role.model.dto.RoleCreateDTO;
import com.atlas.system.role.model.entity.Role;
import com.atlas.system.role.model.entity.RolePermission;
import com.atlas.system.role.service.RoleService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色服务实现类
 *
 * <p>实现角色创建和关联业务逻辑。
 *
 * <p>功能说明：
 *
 * <ul>
 *   <li>实现 createRole 方法，保存角色到数据库
 *   <li>实现 assignPermissionToRole 方法，保存角色权限关联到数据库
 *   <li>处理角色代码重复的情况，抛出 BusinessException
 *   <li>处理角色或权限不存在的情况，抛出适当的异常
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleMapper roleMapper;
  private final RolePermissionMapper rolePermissionMapper;
  private final PermissionMapper permissionMapper;

  /**
   * 创建角色
   *
   * @param roleCreateDTO 角色创建 DTO
   * @return 角色ID
   * @throws BusinessException 如果角色代码已存在，错误码：032005
   */
  @Override
  @Transactional
  public Long createRole(RoleCreateDTO roleCreateDTO) {
    // 检查角色代码是否已存在
    Role existingRole =
        roleMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, roleCreateDTO.getRoleCode()));
    if (existingRole != null && !"DELETED".equals(existingRole.getStatus())) {
      throw new BusinessException(SystemErrorCode.ROLE_CODE_ALREADY_EXISTS, "角色代码已存在");
    }
    // 创建角色实体
    Role role = new Role();
    role.setRoleCode(roleCreateDTO.getRoleCode());
    role.setRoleName(roleCreateDTO.getRoleName());
    role.setDescription(roleCreateDTO.getDescription());
    role.setStatus("ACTIVE");
    role.setCreatedAt(LocalDateTime.now());
    role.setUpdatedAt(LocalDateTime.now());
    // 保存角色
    roleMapper.insert(role);
    return role.getRoleId();
  }

  /**
   * 为角色分配权限
   *
   * @param roleId 角色ID
   * @param permissionId 权限ID
   * @throws BusinessException 如果角色或权限不存在，错误码：032101 或 032201
   */
  @Override
  @Transactional
  public void assignPermissionToRole(Long roleId, Long permissionId) {
    // 检查角色是否存在
    Role role = roleMapper.selectById(roleId);
    if (role == null || "DELETED".equals(role.getStatus())) {
      throw new BusinessException(SystemErrorCode.ROLE_NOT_FOUND, "角色不存在");
    }
    // 检查权限是否存在
    Permission permission = permissionMapper.selectById(permissionId);
    if (permission == null || "DELETED".equals(permission.getStatus())) {
      throw new BusinessException(SystemErrorCode.PERMISSION_NOT_FOUND, "权限不存在");
    }
    // 检查关联是否已存在
    RolePermission existingRolePermission =
        rolePermissionMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, roleId)
                .eq(RolePermission::getPermissionId, permissionId));
    if (existingRolePermission != null) {
      return; // 关联已存在，直接返回
    }
    // 创建角色权限关联
    RolePermission rolePermission = new RolePermission();
    rolePermission.setRoleId(roleId);
    rolePermission.setPermissionId(permissionId);
    rolePermission.setCreatedAt(LocalDateTime.now());
    rolePermissionMapper.insert(rolePermission);
  }
}
