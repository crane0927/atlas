/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.menu.model.vo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 菜单树 VO
 *
 * <p>用于返回菜单树结构给前端。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class MenuTreeVO {

  private String menuId;
  private String parentId;
  private String name;
  private String path;
  private String component;
  private String redirect;
  private String icon;
  private String type;
  private Integer sort;
  private Boolean visible;
  private Boolean keepAlive;
  private Boolean external;
  private String permissionCode;
  private String status;
  private List<MenuTreeVO> children = new ArrayList<>();
}
