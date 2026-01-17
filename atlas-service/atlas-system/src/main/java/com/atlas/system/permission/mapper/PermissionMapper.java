/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.mapper;

import com.atlas.system.permission.model.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限 Mapper 接口
 *
 * <p>提供权限数据访问方法，继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {}
