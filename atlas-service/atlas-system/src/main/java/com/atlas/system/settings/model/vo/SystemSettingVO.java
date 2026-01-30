/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.model.vo;

import com.atlas.system.settings.model.enums.SystemSettingType;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 系统设置展示 VO
 *
 * <p>用于返回系统设置列表的数据结构。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>key：设置项唯一标识
 *   <li>value：设置项值
 *   <li>type：设置项类型（SYSTEM/CUSTOM）
 *   <li>createTime：创建时间
 *   <li>updateTime：更新时间
 *   <li>createBy：创建人
 *   <li>updateBy：更新人
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class SystemSettingVO {

  /** 设置项唯一标识 */
  private String key;

  /** 设置项值 */
  private String value;

  /** 设置项类型（SYSTEM/CUSTOM） */
  private SystemSettingType type;

  /** 创建时间 */
  private LocalDateTime createTime;

  /** 更新时间 */
  private LocalDateTime updateTime;

  /** 创建人 */
  private String createBy;

  /** 更新人 */
  private String updateBy;
}
