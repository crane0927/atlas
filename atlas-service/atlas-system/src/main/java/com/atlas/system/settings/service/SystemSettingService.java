/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.settings.service;

import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.settings.model.dto.SystemSettingCreateDTO;
import com.atlas.system.settings.model.dto.SystemSettingQueryDTO;
import com.atlas.system.settings.model.dto.SystemSettingUpdateDTO;
import com.atlas.system.settings.model.vo.SystemSettingVO;
import java.util.List;

/**
 * 系统设置服务接口
 *
 * <p>提供系统默认设置的业务能力定义。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface SystemSettingService {

  /**
   * 查询设置项列表
   *
   * <p>支持按类型与关键字过滤。
   *
   * @param queryDTO 查询参数
   * @return 设置项列表
   */
  List<SystemSettingVO> listSettings(SystemSettingQueryDTO queryDTO);

  /**
   * 分页查询设置项列表
   *
   * <p>支持按类型与关键字过滤；支持排序（排序字段白名单：key、createTime、updateTime）。{@link SystemSettingQueryDTO} 继承
   * PageQueryDTO，含 page、size、sort。
   *
   * @param queryDTO 查询参数（含 page、size、sort）
   * @return 分页结果
   */
  PageResult<SystemSettingVO> listSettingsPage(SystemSettingQueryDTO queryDTO);

  /**
   * 创建自定义设置项
   *
   * @param createDTO 创建请求
   * @return 创建后的设置项
   */
  SystemSettingVO createSetting(SystemSettingCreateDTO createDTO);

  /**
   * 修改设置项 value
   *
   * <p>仅允许更新 value 字段，不允许修改 key。
   *
   * @param key 设置项 key
   * @param updateDTO 更新请求
   * @return 更新后的设置项
   */
  SystemSettingVO updateSettingValue(String key, SystemSettingUpdateDTO updateDTO);

  /**
   * 删除自定义设置项
   *
   * @param key 设置项 key
   */
  void deleteCustomSetting(String key);
}
