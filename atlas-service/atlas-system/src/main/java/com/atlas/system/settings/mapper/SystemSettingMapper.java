/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.mapper;

import com.atlas.system.settings.model.entity.SystemSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统设置 Mapper 接口
 *
 * <p>提供系统设置数据访问方法，继承 MyBatis-Plus 的 BaseMapper 提供基础 CRUD 功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Mapper
public interface SystemSettingMapper extends BaseMapper<SystemSetting> {}
