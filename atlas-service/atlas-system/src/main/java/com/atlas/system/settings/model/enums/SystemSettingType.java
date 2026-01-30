/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 系统设置类型枚举
 *
 * <p>用于区分系统默认设置与自定义设置。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Getter
public enum SystemSettingType {
  /** 系统类型设置 */
  SYSTEM("SYSTEM"),

  /** 自定义类型设置 */
  CUSTOM("CUSTOM");

  @EnumValue private final String code;

  SystemSettingType(String code) {
    this.code = code;
  }
}
