/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.menu.model.entity;

import com.atlas.common.infra.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体类
 *
 * <p>对应数据库表：sys_menu
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>menuId：菜单ID
 *   <li>parentId：父菜单ID
 *   <li>name：菜单名称
 *   <li>path：路由路径
 *   <li>component：前端组件标识
 *   <li>redirect：路由重定向
 *   <li>icon：图标
 *   <li>type：菜单类型（DIR/MENU/BUTTON）
 *   <li>sort：排序号
 *   <li>visible：是否显示
 *   <li>keepAlive：是否缓存
 *   <li>external：是否外链
 *   <li>permissionCode：权限码
 *   <li>status：菜单状态
 * </ul>
 *
 * <p>审计与逻辑删除字段来自 {@link BaseEntity}。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class Menu extends BaseEntity {

  /** 菜单ID */
  @TableId(type = IdType.ASSIGN_ID)
  private Long menuId;

  /** 父菜单ID */
  private Long parentId;

  /** 菜单名称 */
  private String name;

  /** 路由路径 */
  private String path;

  /** 前端组件标识 */
  private String component;

  /** 路由重定向 */
  private String redirect;

  /** 图标 */
  private String icon;

  /** 菜单类型（DIR/MENU/BUTTON） */
  private String type;

  /** 排序号 */
  private Integer sort;

  /** 是否显示 */
  private Boolean visible;

  /** 是否缓存 */
  private Boolean keepAlive;

  /** 是否外链 */
  private Boolean external;

  /** 权限码 */
  private String permissionCode;

  /** 菜单状态 */
  private String status;
}
