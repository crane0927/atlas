/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.mapper;

import com.atlas.system.user.model.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户角色关联 Mapper 接口
 *
 * <p>提供用户角色关联数据访问方法，继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 功能。
 *
 * <p>自定义查询方法：
 *
 * <ul>
 *   <li>selectRoleCodesByUserId：根据用户ID查询角色代码列表
 *   <li>selectRoleIdsByUserId：根据用户ID查询角色ID列表
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

  /**
   * 根据用户ID查询角色代码列表
   *
   * <p>通过用户ID查询用户拥有的所有角色代码，用于权限查询。
   *
   * @param userId 用户ID
   * @return 角色代码列表，如果用户没有角色则返回空列表
   */
  @Select(
      "SELECT r.role_code FROM sys_role r "
          + "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id "
          + "WHERE ur.user_id = #{userId} AND r.status = 'ACTIVE'")
  List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID查询角色ID列表
   *
   * <p>通过用户ID查询用户拥有的所有角色ID，用于权限查询。
   *
   * @param userId 用户ID
   * @return 角色ID列表，如果用户没有角色则返回空列表
   */
  @Select(
      "SELECT ur.role_id FROM sys_user_role ur "
          + "INNER JOIN sys_role r ON ur.role_id = r.role_id "
          + "WHERE ur.user_id = #{userId} AND r.status = 'ACTIVE'")
  List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}
