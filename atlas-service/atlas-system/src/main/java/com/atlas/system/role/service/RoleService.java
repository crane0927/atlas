/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.role.service;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.role.model.dto.RoleCreateDTO;
import com.atlas.system.role.model.dto.RoleQueryDTO;
import com.atlas.system.role.model.dto.RoleUpdateDTO;
import com.atlas.system.role.model.vo.RoleListVO;

/**
 * 角色服务接口
 *
 * <p>提供角色创建和关联业务逻辑。
 *
 * <p>方法说明：
 *
 * <ul>
 *   <li>createRole：创建角色
 *   <li>assignPermissionToRole：为角色分配权限
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface RoleService {

  /**
   * 根据角色ID查询角色详情
   *
   * @param roleId 角色ID
   * @return 角色列表项 VO（与详情结构一致）
   * @throws BusinessException 如果角色不存在或已删除，错误码：032101
   */
  RoleListVO getRoleById(String roleId);

  /**
   * 创建角色
   *
   * <p>根据角色创建 DTO 创建新角色。
   *
   * @param roleCreateDTO 角色创建 DTO
   * @return 角色ID
   * @throws BusinessException 如果角色代码已存在，错误码：032105
   */
  String createRole(RoleCreateDTO roleCreateDTO);

  /**
   * 更新角色
   *
   * <p>仅更新 roleName、description、status；不修改 roleCode。
   *
   * @param roleId 角色ID
   * @param roleUpdateDTO 更新 DTO
   * @throws BusinessException 如果角色不存在或已删除，错误码：032101
   */
  void updateRole(String roleId, RoleUpdateDTO roleUpdateDTO);

  /**
   * 逻辑删除角色
   *
   * <p>将角色 status 置为 DELETED。
   *
   * @param roleId 角色ID
   * @throws BusinessException 如果角色不存在或已删除，错误码：032101
   */
  void deleteRole(String roleId);

  /**
   * 为角色分配权限
   *
   * <p>建立角色与权限的关联关系。若关联已存在则直接返回（幂等）。
   *
   * @param roleId 角色ID
   * @param permissionId 权限ID
   * @throws BusinessException 如果角色或权限不存在，错误码：032101 或 032201
   */
  void assignPermissionToRole(String roleId, String permissionId);

  /**
   * 移除角色与权限的关联
   *
   * @param roleId 角色ID
   * @param permissionId 权限ID
   * @throws BusinessException 如果角色或权限不存在，错误码：032101 或 032201
   */
  void removePermissionFromRole(String roleId, String permissionId);

  /**
   * 分页查询角色列表
   *
   * <p>支持按角色代码、角色名称、状态筛选，以及排序（排序字段白名单：roleCode、roleName、createdAt，兼容 createTime/createdAt）。{@link RoleQueryDTO}
   * 继承 PageQueryDTO，含 page、size、sort。
   *
   * @param query 查询条件（含 page、size、sort）
   * @return 分页结果
   */
  PageResult<RoleListVO> listRolesPage(RoleQueryDTO query);
}
