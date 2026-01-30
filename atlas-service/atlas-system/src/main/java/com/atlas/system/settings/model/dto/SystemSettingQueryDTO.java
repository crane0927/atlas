/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.model.dto;

import com.atlas.system.settings.model.enums.SystemSettingType;
import lombok.Data;

/**
 * 系统设置查询 DTO
 *
 * <p>用于查询系统设置列表的请求参数封装。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>type：设置项类型（SYSTEM/CUSTOM），可选
 *   <li>keyword：设置项 key 关键字，可选
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class SystemSettingQueryDTO {

  /** 设置项类型（SYSTEM/CUSTOM），可选 */
  private SystemSettingType type;

  /** 设置项 key 关键字，可选 */
  private String keyword;
}
