/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统设置更新 DTO
 *
 * <p>用于更新设置项 value。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class SystemSettingUpdateDTO {

  /** 设置项值 */
  @NotBlank(message = "设置项值不能为空")
  private String value;
}
