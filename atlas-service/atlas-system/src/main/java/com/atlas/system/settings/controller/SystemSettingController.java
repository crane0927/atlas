/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.controller;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.settings.model.dto.SystemSettingQueryDTO;
import com.atlas.system.settings.model.dto.SystemSettingUpdateDTO;
import com.atlas.system.settings.model.vo.SystemSettingVO;
import com.atlas.system.settings.service.SystemSettingService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统设置控制器
 *
 * <p>提供系统默认设置的查询接口。
 *
 * <p>接口说明：
 *
 * <ul>
 *   <li>GET /api/v1/system-settings：查询设置项列表
 *   <li>PUT /api/v1/system-settings/{key}：修改设置项 value
 * </ul>
 *
 * <p>返回格式：统一使用 {@link Result} 包装响应数据
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/system-settings")
@RequiredArgsConstructor
public class SystemSettingController {

  private final SystemSettingService systemSettingService;

  /**
   * 查询设置项列表
   *
   * <p>支持按类型与关键字过滤。
   *
   * @param queryDTO 查询参数
   * @return 设置项列表，使用 {@link Result} 包装
   */
  @GetMapping
  public Result<List<SystemSettingVO>> listSystemSettings(SystemSettingQueryDTO queryDTO) {
    List<SystemSettingVO> settings = systemSettingService.listSettings(queryDTO);
    return Result.success(settings);
  }

  /**
   * 修改设置项 value
   *
   * <p>仅允许更新 value 字段，不允许修改 key。
   *
   * @param key 设置项 key
   * @param updateDTO 更新请求
   * @return 更新后的设置项
   */
  @PutMapping("/{key}")
  public Result<SystemSettingVO> updateSystemSetting(
      @PathVariable String key, @Valid @RequestBody SystemSettingUpdateDTO updateDTO) {
    SystemSettingVO setting = systemSettingService.updateSettingValue(key, updateDTO);
    return Result.success(setting);
  }
}
