/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.role.mapper;

import com.atlas.system.role.model.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 角色权限关联 Mapper 接口
 *
 * <p>提供角色权限关联数据访问方法，继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 功能。
 *
 * <p>自定义查询方法：
 *
 * <ul>
 *   <li>selectPermissionCodesByRoleIds：根据角色ID列表查询权限代码列表
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

  /**
   * 根据角色ID列表查询权限代码列表
   *
   * <p>通过角色ID列表查询这些角色拥有的所有权限代码，用于权限查询。
   *
   * @param roleIds 角色ID列表
   * @return 权限代码列表（去重），如果角色没有权限则返回空列表
   */
  @Select(
      "<script>"
          + "SELECT DISTINCT p.permission_code FROM sys_permission p "
          + "INNER JOIN sys_role_permission rp ON p.permission_id = rp.permission_id AND rp.deleted = 0 "
          + "WHERE rp.role_id IN "
          + "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>"
          + "#{roleId}"
          + "</foreach>"
          + " AND p.status = 'ACTIVE'"
          + "</script>")
  List<String> selectPermissionCodesByRoleIds(@Param("roleIds") List<Long> roleIds);
}
