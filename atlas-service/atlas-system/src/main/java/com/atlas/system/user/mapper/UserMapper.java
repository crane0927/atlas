/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.mapper;

import com.atlas.system.user.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper 接口
 *
 * <p>提供用户数据访问方法，继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 功能。
 *
 * <p>自定义查询方法：
 *
 * <ul>
 *   <li>selectByUsername：根据用户名查询用户
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

  /**
   * 根据用户名查询用户
   *
   * <p>通过用户名查询用户信息，用于用户认证场景。
   *
   * @param username 用户名
   * @return 用户实体，如果不存在则返回 null
   */
  @Select("SELECT * FROM sys_user WHERE username = #{username} AND status != 'DELETED'")
  User selectByUsername(@Param("username") String username);
}
