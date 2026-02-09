/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.settings.model.dto;

import com.atlas.common.feature.core.page.PageQueryDTO;
import com.atlas.system.settings.model.enums.SystemSettingType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统设置查询 DTO
 *
 * <p>继承 {@link PageQueryDTO}，用于查询/分页查询系统设置列表。分页接口使用 page、size、sort；非分页接口忽略分页字段。
 *
 * <p>业务字段说明：
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
@EqualsAndHashCode(callSuper = true)
public class SystemSettingQueryDTO extends PageQueryDTO {

  /** 设置项类型（SYSTEM/CUSTOM），可选 */
  private SystemSettingType type;

  /** 设置项 key 关键字，可选 */
  private String keyword;
}
