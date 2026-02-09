/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.settings.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 系统设置创建 DTO
 *
 * <p>用于创建自定义设置项。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class SystemSettingCreateDTO {

  /** 设置项 key */
  @NotBlank(message = "设置项 key 不能为空")
  @Size(max = 100, message = "设置项 key 长度不能超过 100 个字符")
  private String key;

  /** 设置项 value */
  @NotBlank(message = "设置项 value 不能为空")
  private String value;
}
