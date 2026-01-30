/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.service.impl;

import com.atlas.system.settings.mapper.SystemSettingMapper;
import com.atlas.system.settings.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统设置服务实现类
 *
 * <p>提供系统默认设置的业务逻辑实现，具体方法在后续阶段补充。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

  private final SystemSettingMapper systemSettingMapper;
}
