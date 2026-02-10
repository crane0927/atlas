/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.settings.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.settings.mapper.SystemSettingMapper;
import com.atlas.system.settings.model.dto.SystemSettingCreateDTO;
import com.atlas.system.settings.model.dto.SystemSettingQueryDTO;
import com.atlas.system.settings.model.dto.SystemSettingUpdateDTO;
import com.atlas.system.settings.model.entity.SystemSetting;
import com.atlas.system.settings.model.enums.SystemSettingType;
import com.atlas.system.settings.model.vo.SystemSettingVO;
import com.atlas.system.settings.service.SystemSettingService;
import com.atlas.system.util.SortHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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

  /** 列表查询最大条数，防止全表加载与响应过大 */
  private static final int LIST_SETTINGS_MAX_SIZE = 500;

  private final SystemSettingMapper systemSettingMapper;

  @Override
  public SystemSettingVO getSettingByKey(String key) {
    SystemSetting setting =
        systemSettingMapper.selectOne(
            new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getKey, key));
    if (setting == null) {
      throw new BusinessException(SystemErrorCode.SYSTEM_SETTING_NOT_FOUND, "设置项不存在");
    }
    return convertToVO(setting);
  }

  /**
   * 查询设置项列表
   *
   * <p>最多返回 {@value #LIST_SETTINGS_MAX_SIZE} 条，超过时请使用分页接口 listSettingsPage。
   *
   * @param queryDTO 查询参数
   * @return 设置项列表
   */
  @Override
  public List<SystemSettingVO> listSettings(SystemSettingQueryDTO queryDTO) {
    LambdaQueryWrapper<SystemSetting> wrapper = buildQueryWrapper(queryDTO);
    applySort(wrapper, Optional.ofNullable(queryDTO).map(SystemSettingQueryDTO::getSort).orElse(null));
    Page<SystemSetting> pageRequest = new Page<>(1, LIST_SETTINGS_MAX_SIZE);
    Page<SystemSetting> resultPage = systemSettingMapper.selectPage(pageRequest, wrapper);
    return resultPage.getRecords().stream()
        .filter(Objects::nonNull)
        .map(this::convertToVO)
        .collect(Collectors.toList());
  }

  /**
   * 分页查询设置项列表
   *
   * <p>与 PageQueryDTO 语义一致：page 默认 1，size 默认 10；支持 sort 参数（白名单：key、createdAt、updatedAt，兼容 createTime/updateTime）。
   *
   * @param queryDTO 查询参数
   * @return 分页结果
   */
  @Override
  public PageResult<SystemSettingVO> listSettingsPage(SystemSettingQueryDTO queryDTO) {
    int pageNumber =
        Optional.ofNullable(queryDTO).map(SystemSettingQueryDTO::getPageSafe).orElse(1);
    int pageSize = Optional.ofNullable(queryDTO).map(SystemSettingQueryDTO::getSizeSafe).orElse(10);
    String sort = Optional.ofNullable(queryDTO).map(SystemSettingQueryDTO::getSort).orElse(null);
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
        systemSettingMapper.selectOne(
            new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getKey, key));
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
        systemSettingMapper.selectOne(
            new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getKey, key));
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
        systemSettingMapper.selectOne(
            new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getKey, key));
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
    Optional.ofNullable(queryDTO)
        .ifPresent(
            q -> {
              if (q.getType() != null) queryWrapper.eq(SystemSetting::getType, q.getType());
              if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
                queryWrapper.like(SystemSetting::getKey, q.getKeyword().trim());
              }
            });
    return queryWrapper;
  }

  private static final Map<String, BiConsumer<LambdaQueryWrapper<SystemSetting>, Boolean>>
      SETTING_SORT_FIELDS = new HashMap<>();

  static {
    SETTING_SORT_FIELDS.put("key", (w, asc) -> w.orderBy(true, asc, SystemSetting::getKey));
    SETTING_SORT_FIELDS.put(
        "createdat", (w, asc) -> w.orderBy(true, asc, SystemSetting::getCreatedAt));
    SETTING_SORT_FIELDS.put(
        "createtime", (w, asc) -> w.orderBy(true, asc, SystemSetting::getCreatedAt));
    SETTING_SORT_FIELDS.put(
        "updatedat", (w, asc) -> w.orderBy(true, asc, SystemSetting::getUpdatedAt));
    SETTING_SORT_FIELDS.put(
        "updatetime", (w, asc) -> w.orderBy(true, asc, SystemSetting::getUpdatedAt));
  }

  /** 应用排序（白名单：key、createdAt、updatedAt，兼容 createTime/updateTime） */
  private void applySort(LambdaQueryWrapper<SystemSetting> wrapper, String sort) {
    SortHelper.applySort(
        wrapper, sort, w -> w.orderByDesc(SystemSetting::getCreatedAt), SETTING_SORT_FIELDS);
  }

  /** 将 SystemSetting 实体转换为 SystemSettingVO（原则 20：使用 BeanUtils，Entity 与 VO 字段名一致） */
  private SystemSettingVO convertToVO(SystemSetting setting) {
    SystemSettingVO vo = new SystemSettingVO();
    BeanUtils.copyProperties(setting, vo);
    return vo;
  }
}
