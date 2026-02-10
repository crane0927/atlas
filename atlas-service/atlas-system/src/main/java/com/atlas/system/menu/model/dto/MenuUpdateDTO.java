/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.menu.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单更新 DTO
 *
 * <p>用于更新菜单的请求数据传输对象。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class MenuUpdateDTO {

  /** 父菜单ID */
  private String parentId;

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

  /** 菜单状态 */
  @Size(max = 20, message = "状态长度不能超过 20 个字符")
  private String status;
}
