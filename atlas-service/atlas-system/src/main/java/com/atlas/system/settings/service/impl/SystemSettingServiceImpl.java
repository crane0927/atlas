/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.settings.mapper.SystemSettingMapper;
import com.atlas.system.settings.model.dto.SystemSettingCreateDTO;
import com.atlas.system.settings.model.dto.SystemSettingQueryDTO;
import com.atlas.system.settings.model.dto.SystemSettingUpdateDTO;
import com.atlas.system.settings.model.entity.SystemSetting;
import com.atlas.system.settings.model.enums.SystemSettingType;
import com.atlas.system.settings.model.vo.SystemSettingVO;
import com.atlas.system.settings.service.SystemSettingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    return systemSettingMapper.selectList(buildQueryWrapper(queryDTO)).stream()
        .filter(Objects::nonNull)
        .map(this::convertToVO)
        .collect(Collectors.toList());
  }

  /**
   * 分页查询设置项列表
   *
   * <p>与 PageQueryDTO 语义一致：page 默认 1，size 默认 10；支持 sort 参数（白名单：key、createTime、updateTime）。
   *
   * @param queryDTO 查询参数
   * @param page 页码（从 1 开始）
   * @param size 每页大小
   * @param sort 排序，格式：字段名,asc 或 字段名,desc，可选
   * @return 分页结果
   */
  @Override
  public PageResult<SystemSettingVO> listSettingsPage(SystemSettingQueryDTO queryDTO) {
    int pageNumber = queryDTO != null ? queryDTO.getPageSafe() : 1;
    int pageSize = queryDTO != null ? queryDTO.getSizeSafe() : 10;
    String sort = queryDTO != null ? queryDTO.getSort() : null;
    LambdaQueryWrapper<SystemSetting> wrapper = buildQueryWrapper(queryDTO);
    applySort(wrapper, sort);
    Page<SystemSetting> pageRequest = new Page<>(pageNumber, pageSize);
    Page<SystemSetting> resultPage = systemSettingMapper.selectPage(pageRequest, wrapper);
    List<SystemSettingVO> records =
        resultPage.getRecords().stream()
            .filter(Objects::nonNull)
            .map(this::convertToVO)
            .collect(Collectors.toList());
    return PageResult.of(records, resultPage.getTotal(), pageNumber, pageSize);
  }

  /**
   * 创建自定义设置项
   *
   * @param createDTO 创建请求
   * @return 创建后的设置项
   */
  @Override
  public SystemSettingVO createSetting(SystemSettingCreateDTO createDTO) {
    String key = createDTO.getKey().trim();
    SystemSetting existing =
        systemSettingMapper.selectOne(new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getKey, key));
    if (existing != null) {
      throw new BusinessException(SystemErrorCode.SYSTEM_SETTING_KEY_ALREADY_EXISTS, "设置项 key 已存在");
    }
    SystemSetting setting = new SystemSetting();
    setting.setKey(key);
    setting.setValue(createDTO.getValue());
    setting.setType(SystemSettingType.CUSTOM);
    systemSettingMapper.insert(setting);
    return convertToVO(setting);
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

  /**
   * 删除自定义设置项
   *
   * @param key 设置项 key
   */
  @Override
  public void deleteCustomSetting(String key) {
    SystemSetting setting =
        systemSettingMapper.selectOne(new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getKey, key));
    if (setting == null) {
      throw new BusinessException(SystemErrorCode.SYSTEM_SETTING_NOT_FOUND, "设置项不存在");
    }
    if (SystemSettingType.SYSTEM.equals(setting.getType())) {
      throw new BusinessException(
          SystemErrorCode.SYSTEM_SETTING_SYSTEM_DELETE_FORBIDDEN, "系统默认设置不可删除");
    }
    systemSettingMapper.deleteById(setting.getSettingId());
  }

  private LambdaQueryWrapper<SystemSetting> buildQueryWrapper(SystemSettingQueryDTO queryDTO) {
    LambdaQueryWrapper<SystemSetting> queryWrapper = new LambdaQueryWrapper<>();
    if (queryDTO != null && queryDTO.getType() != null) {
      queryWrapper.eq(SystemSetting::getType, queryDTO.getType());
    }
    if (queryDTO != null && queryDTO.getKeyword() != null && !queryDTO.getKeyword().isBlank()) {
      queryWrapper.like(SystemSetting::getKey, queryDTO.getKeyword().trim());
    }
    return queryWrapper;
  }

  /**
   * 应用排序（白名单：key、createTime、updateTime），与 PageQueryDTO 规范一致
   *
   * @param wrapper 查询包装器
   * @param sort 排序字符串，格式：字段名,asc 或 字段名,desc
   */
  private void applySort(LambdaQueryWrapper<SystemSetting> wrapper, String sort) {
    if (!StringUtils.hasText(sort)) {
      wrapper.orderByDesc(SystemSetting::getCreateTime);
      return;
    }
    String[] parts = sort.split(",");
    String field = parts.length > 0 ? parts[0].trim() : "";
    boolean asc = parts.length <= 1 || !"desc".equalsIgnoreCase(parts[1].trim());
    if ("key".equalsIgnoreCase(field)) {
      wrapper.orderBy(true, asc, SystemSetting::getKey);
    } else if ("createTime".equalsIgnoreCase(field)) {
      wrapper.orderBy(true, asc, SystemSetting::getCreateTime);
    } else if ("updateTime".equalsIgnoreCase(field)) {
      wrapper.orderBy(true, asc, SystemSetting::getUpdateTime);
    } else {
      wrapper.orderByDesc(SystemSetting::getCreateTime);
    }
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
