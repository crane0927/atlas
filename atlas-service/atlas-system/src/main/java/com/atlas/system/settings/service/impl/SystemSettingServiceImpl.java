/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.settings.mapper.SystemSettingMapper;
import com.atlas.system.settings.model.dto.SystemSettingQueryDTO;
import com.atlas.system.settings.model.dto.SystemSettingUpdateDTO;
import com.atlas.system.settings.model.entity.SystemSetting;
import com.atlas.system.settings.model.vo.SystemSettingVO;
import com.atlas.system.settings.service.SystemSettingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

  /**
   * 查询设置项列表
   *
   * @param queryDTO 查询参数
   * @return 设置项列表
   */
  @Override
  public List<SystemSettingVO> listSettings(SystemSettingQueryDTO queryDTO) {
    LambdaQueryWrapper<SystemSetting> queryWrapper = new LambdaQueryWrapper<>();
    if (queryDTO != null && queryDTO.getType() != null) {
      queryWrapper.eq(SystemSetting::getType, queryDTO.getType());
    }
    if (queryDTO != null && queryDTO.getKeyword() != null && !queryDTO.getKeyword().isBlank()) {
      queryWrapper.like(SystemSetting::getKey, queryDTO.getKeyword().trim());
    }
    return systemSettingMapper.selectList(queryWrapper).stream()
        .filter(Objects::nonNull)
        .map(this::convertToVO)
        .collect(Collectors.toList());
  }

  /**
   * 修改设置项 value
   *
   * @param key 设置项 key
   * @param updateDTO 更新请求
   * @return 更新后的设置项
   */
  @Override
  public SystemSettingVO updateSettingValue(String key, SystemSettingUpdateDTO updateDTO) {
    SystemSetting setting =
        systemSettingMapper.selectOne(new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getKey, key));
    if (setting == null) {
      throw new BusinessException(SystemErrorCode.SYSTEM_SETTING_NOT_FOUND, "设置项不存在");
    }
    setting.setValue(updateDTO.getValue());
    systemSettingMapper.updateById(setting);
    return convertToVO(setting);
  }

  private SystemSettingVO convertToVO(SystemSetting setting) {
    SystemSettingVO vo = new SystemSettingVO();
    vo.setKey(setting.getKey());
    vo.setValue(setting.getValue());
    vo.setType(setting.getType());
    vo.setCreateTime(setting.getCreateTime());
    vo.setUpdateTime(setting.getUpdateTime());
    vo.setCreateBy(setting.getCreateBy());
    vo.setUpdateBy(setting.getUpdateBy());
    return vo;
  }
}
