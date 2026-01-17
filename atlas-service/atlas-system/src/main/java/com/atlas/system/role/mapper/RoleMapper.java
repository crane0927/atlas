/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.role.mapper;

import com.atlas.system.role.model.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper 接口
 *
 * <p>提供角色数据访问方法，继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {}
