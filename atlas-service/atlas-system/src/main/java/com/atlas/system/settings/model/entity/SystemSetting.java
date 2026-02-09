/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.settings.model.entity;

import com.atlas.common.infra.db.entity.BaseEntity;
import com.atlas.system.settings.model.enums.SystemSettingType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统设置实体类
 *
 * <p>对应数据库表：sys_system_setting
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>settingId：设置项ID，主键，自增
 *   <li>key：设置项唯一标识
 *   <li>value：设置项值
 *   <li>type：设置项类型（SYSTEM/CUSTOM）
 * </ul>
 *
 * <p>审计字段与逻辑删除字段来自 {@link BaseEntity}。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_system_setting")
public class SystemSetting extends BaseEntity {

  /** 设置项ID，主键，自增 */
  @TableId(type = IdType.AUTO)
  private Long settingId;

  /** 设置项唯一标识 */
  @TableField("setting_key")
  private String key;

  /** 设置项值 */
  @TableField("setting_value")
  private String value;

  /** 设置项类型（SYSTEM/CUSTOM） */
  @TableField("setting_type")
  private SystemSettingType type;
}
