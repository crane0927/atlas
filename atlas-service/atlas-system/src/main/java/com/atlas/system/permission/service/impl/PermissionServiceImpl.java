/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.permission.mapper.PermissionMapper;
import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.permission.model.dto.PermissionCreateDTO;
import com.atlas.system.permission.model.dto.PermissionQueryDTO;
import com.atlas.system.permission.model.entity.Permission;
import com.atlas.system.permission.model.vo.PermissionListVO;
import com.atlas.system.permission.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.atlas.system.role.mapper.RolePermissionMapper;
import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.mapper.UserRoleMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
  private final PermissionMapper permissionMapper;

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
    return Optional.ofNullable(roleCodes).orElse(Collections.emptyList());
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
    return Optional.ofNullable(permissionCodes).orElse(Collections.emptyList());
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
    dto.setRoles(Optional.ofNullable(roles).orElse(new ArrayList<>()));
    dto.setPermissions(Optional.ofNullable(permissions).orElse(new ArrayList<>()));
    return dto;
  }

  /**
   * 创建权限
   *
   * @param permissionCreateDTO 权限创建 DTO
   * @return 权限ID
   * @throws BusinessException 如果权限代码已存在，错误码：032006
   */
  @Override
  @Transactional
  public Long createPermission(PermissionCreateDTO permissionCreateDTO) {
    // 检查权限代码是否已存在
    Permission existingPermission =
        permissionMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Permission>()
                .eq(Permission::getPermissionCode, permissionCreateDTO.getPermissionCode()));
    if (existingPermission != null && !"DELETED".equals(existingPermission.getStatus())) {
      throw new BusinessException(SystemErrorCode.PERMISSION_CODE_ALREADY_EXISTS, "权限代码已存在");
    }
    // 创建权限实体
    Permission permission = new Permission();
    permission.setPermissionCode(permissionCreateDTO.getPermissionCode());
    permission.setPermissionName(permissionCreateDTO.getPermissionName());
    permission.setDescription(permissionCreateDTO.getDescription());
    permission.setStatus("ACTIVE");
    permission.setCreatedAt(LocalDateTime.now());
    permission.setUpdatedAt(LocalDateTime.now());
    // 保存权限
    permissionMapper.insert(permission);
    return permission.getPermissionId();
  }

  /**
   * 分页查询权限列表
   *
   * @param query 查询条件
   * @return 分页结果
   */
  @Override
  public PageResult<PermissionListVO> listPermissionsPage(PermissionQueryDTO query) {
    int pageNum = Optional.ofNullable(query).map(PermissionQueryDTO::getPageSafe).orElse(1);
    int pageSize = Optional.ofNullable(query).map(PermissionQueryDTO::getSizeSafe).orElse(10);
    String sort = Optional.ofNullable(query).map(PermissionQueryDTO::getSort).orElse(null);

    LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
    wrapper.ne(Permission::getStatus, "DELETED");
    Optional.ofNullable(query).ifPresent(q -> {
      if (StringUtils.hasText(q.getPermissionCode())) wrapper.like(Permission::getPermissionCode, q.getPermissionCode());
      if (StringUtils.hasText(q.getPermissionName())) wrapper.like(Permission::getPermissionName, q.getPermissionName());
      if (StringUtils.hasText(q.getStatus())) wrapper.eq(Permission::getStatus, q.getStatus());
    });
    applySort(wrapper, sort);

    Page<Permission> pageReq = new Page<>(pageNum, pageSize);
    Page<Permission> resultPage = permissionMapper.selectPage(pageReq, wrapper);
    List<PermissionListVO> list =
        resultPage.getRecords().stream()
            .map(this::convertToListVO)
            .collect(Collectors.toList());
    return PageResult.of(list, resultPage.getTotal(), pageNum, pageSize);
  }

  /**
   * 应用排序（白名单：permissionCode、permissionName、createTime、createdAt）
   *
   * @param wrapper 查询包装器
   * @param sort 排序字符串，格式：字段名,asc 或 字段名,desc
   */
  private void applySort(LambdaQueryWrapper<Permission> wrapper, String sort) {
    if (!StringUtils.hasText(sort)) {
      wrapper.orderByDesc(Permission::getCreatedAt);
      return;
    }
    String[] parts = sort.split(",");
    String field = parts.length > 0 ? parts[0].trim() : "";
    boolean asc = parts.length <= 1 || !"desc".equalsIgnoreCase(parts[1].trim());
    if ("permissionCode".equalsIgnoreCase(field)) {
      wrapper.orderBy(true, asc, Permission::getPermissionCode);
    } else if ("permissionName".equalsIgnoreCase(field)) {
      wrapper.orderBy(true, asc, Permission::getPermissionName);
    } else if ("createTime".equalsIgnoreCase(field) || "createdAt".equalsIgnoreCase(field)) {
      wrapper.orderBy(true, asc, Permission::getCreatedAt);
    } else {
      wrapper.orderByDesc(Permission::getCreatedAt);
    }
  }

  /**
   * 将 Permission 实体转换为 PermissionListVO（原则 20：使用 BeanUtils）
   *
   * @param permission 权限实体
   * @return 列表项 VO
   */
  private PermissionListVO convertToListVO(Permission permission) {
    PermissionListVO vo = new PermissionListVO();
    BeanUtils.copyProperties(permission, vo);
    return vo;
  }
}
