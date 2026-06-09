/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.permission.mapper.PermissionMapper;
import com.atlas.system.permission.model.dto.PermissionCreateDTO;
import com.atlas.system.permission.model.dto.PermissionQueryDTO;
import com.atlas.system.permission.model.dto.PermissionUpdateDTO;
import com.atlas.system.permission.model.entity.Permission;
import com.atlas.system.permission.model.vo.PermissionListVO;
import com.atlas.system.permission.service.PermissionService;
import com.atlas.system.role.mapper.RolePermissionMapper;
import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.mapper.UserRoleMapper;
import com.atlas.system.util.SortHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
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
  public List<String> getRolesByUserId(String userId) {
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
  public List<String> getPermissionsByUserId(String userId) {
    // 检查用户是否存在
    if (userMapper.selectById(userId) == null) {
      return Collections.emptyList();
    }
    // 查询用户角色ID列表
    List<String> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
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
   * <p>仅做一次用户存在性检查，再分别查询角色码与权限码，避免重复 DB 往返。
   *
   * @param userId 用户ID
   * @return 用户权限信息 DTO
   */
  @Override
  public UserAuthoritiesDTO getAuthoritiesByUserId(String userId) {
    UserAuthoritiesDTO dto = new UserAuthoritiesDTO();
    dto.setUserId(userId);
    dto.setRoles(new ArrayList<>());
    dto.setPermissions(new ArrayList<>());
    if (userMapper.selectById(userId) == null) {
      return dto;
    }
    List<String> roleCodes = userRoleMapper.selectRoleCodesByUserId(userId);
    dto.setRoles(Optional.ofNullable(roleCodes).orElse(Collections.emptyList()));
    List<String> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
    if (roleIds != null && !roleIds.isEmpty()) {
      List<String> permissionCodes = rolePermissionMapper.selectPermissionCodesByRoleIds(roleIds);
      dto.setPermissions(Optional.ofNullable(permissionCodes).orElse(Collections.emptyList()));
    }
    return dto;
  }

  /**
   * 创建权限
   *
   * @param permissionCreateDTO 权限创建 DTO
   * @return 权限ID
   * @throws BusinessException 如果权限代码已存在，错误码：032206
   */
  @Override
  @Transactional
  public String createPermission(PermissionCreateDTO permissionCreateDTO) {
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
    // 保存权限（createdAt/updatedAt 由 AuditMetaObjectHandler 填充）
    permissionMapper.insert(permission);
    return permission.getPermissionId();
  }

  @Override
  public PermissionListVO getPermissionById(String permissionId) {
    Permission permission = permissionMapper.selectById(permissionId);
    if (permission == null || "DELETED".equals(permission.getStatus())) {
      throw new BusinessException(SystemErrorCode.PERMISSION_NOT_FOUND, "权限不存在");
    }
    return convertToListVO(permission);
  }

  @Override
  @Transactional
  public void updatePermission(String permissionId, PermissionUpdateDTO permissionUpdateDTO) {
    Permission permission = permissionMapper.selectById(permissionId);
    if (permission == null || "DELETED".equals(permission.getStatus())) {
      throw new BusinessException(SystemErrorCode.PERMISSION_NOT_FOUND, "权限不存在");
    }
    if (permissionUpdateDTO.getPermissionName() != null) {
      permission.setPermissionName(permissionUpdateDTO.getPermissionName());
    }
    if (permissionUpdateDTO.getDescription() != null) {
      permission.setDescription(permissionUpdateDTO.getDescription());
    }
    if (permissionUpdateDTO.getStatus() != null) {
      permission.setStatus(permissionUpdateDTO.getStatus());
    }
    permissionMapper.updateById(permission);
  }

  @Override
  @Transactional
  public void deletePermission(String permissionId) {
    Permission permission = permissionMapper.selectById(permissionId);
    if (permission == null || "DELETED".equals(permission.getStatus())) {
      throw new BusinessException(SystemErrorCode.PERMISSION_NOT_FOUND, "权限不存在");
    }
    permission.setStatus("DELETED");
    permissionMapper.updateById(permission);
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
    Optional.ofNullable(query)
        .ifPresent(
            q -> {
              if (StringUtils.hasText(q.getPermissionCode()))
                wrapper.like(Permission::getPermissionCode, q.getPermissionCode());
              if (StringUtils.hasText(q.getPermissionName()))
                wrapper.like(Permission::getPermissionName, q.getPermissionName());
              if (StringUtils.hasText(q.getStatus()))
                wrapper.eq(Permission::getStatus, q.getStatus());
            });
    applySort(wrapper, sort);

    Page<Permission> pageReq = new Page<>(pageNum, pageSize);
    Page<Permission> resultPage = permissionMapper.selectPage(pageReq, wrapper);
    List<PermissionListVO> list =
        resultPage.getRecords().stream().map(this::convertToListVO).collect(Collectors.toList());
    return PageResult.of(list, resultPage.getTotal(), pageNum, pageSize);
  }

  private static final Map<String, BiConsumer<LambdaQueryWrapper<Permission>, Boolean>>
      PERMISSION_SORT_FIELDS = new HashMap<>();

  static {
    PERMISSION_SORT_FIELDS.put(
        "permissioncode", (w, asc) -> w.orderBy(true, asc, Permission::getPermissionCode));
    PERMISSION_SORT_FIELDS.put(
        "permissionname", (w, asc) -> w.orderBy(true, asc, Permission::getPermissionName));
    PERMISSION_SORT_FIELDS.put(
        "createdat", (w, asc) -> w.orderBy(true, asc, Permission::getCreatedAt));
    PERMISSION_SORT_FIELDS.put(
        "createtime", (w, asc) -> w.orderBy(true, asc, Permission::getCreatedAt));
  }

  /** 应用排序（白名单：permissionCode、permissionName、createdAt，兼容 createTime/createdAt） */
  private void applySort(LambdaQueryWrapper<Permission> wrapper, String sort) {
    SortHelper.applySort(
        wrapper, sort, w -> w.orderByDesc(Permission::getCreatedAt), PERMISSION_SORT_FIELDS);
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
    vo.setCreatedAt(permission.getCreatedAt());
    return vo;
  }
}
