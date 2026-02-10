/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.menu.mapper;

import com.atlas.system.menu.model.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单 Mapper 接口
 *
 * <p>提供菜单数据访问方法，继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {}
