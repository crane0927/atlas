/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.menu.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单创建 DTO
 *
 * <p>用于创建菜单的请求数据传输对象。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>parentId：父菜单ID，可空（根菜单）
 *   <li>name：菜单名称，必填
 *   <li>path：路由路径，目录/菜单建议填写
 *   <li>component：前端组件标识
 *   <li>redirect：路由重定向
 *   <li>icon：菜单图标
 *   <li>type：菜单类型（DIR/MENU/BUTTON），必填
 *   <li>sort：排序号
 *   <li>visible：是否显示
 *   <li>keepAlive：是否缓存
 *   <li>external：是否外链
 *   <li>permissionCode：权限码
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class MenuCreateDTO {

  /** 父菜单ID */
  private Long parentId;

  /** 菜单名称 */
  @NotBlank(message = "菜单名称不能为空")
  @Size(max = 100, message = "菜单名称长度不能超过 100 个字符")
  private String name;

  /** 路由路径 */
  @Size(max = 255, message = "路由路径长度不能超过 255 个字符")
  private String path;

  /** 前端组件标识 */
  @Size(max = 255, message = "组件标识长度不能超过 255 个字符")
  private String component;

  /** 路由重定向 */
  @Size(max = 255, message = "重定向长度不能超过 255 个字符")
  private String redirect;

  /** 菜单图标 */
  @Size(max = 100, message = "图标长度不能超过 100 个字符")
  private String icon;

  /** 菜单类型（DIR/MENU/BUTTON） */
  @NotBlank(message = "菜单类型不能为空")
  @Size(max = 20, message = "菜单类型长度不能超过 20 个字符")
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
  @Size(max = 100, message = "权限码长度不能超过 100 个字符")
  private String permissionCode;
}
