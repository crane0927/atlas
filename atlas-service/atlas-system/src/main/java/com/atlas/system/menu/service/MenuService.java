/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.menu.service;

import com.atlas.system.menu.model.dto.MenuCreateDTO;
import com.atlas.system.menu.model.dto.MenuUpdateDTO;
import com.atlas.system.menu.model.vo.MenuTreeVO;
import java.util.List;

/**
 * 菜单服务接口
 *
 * <p>提供菜单树查询、创建、更新、删除等功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface MenuService {

  /** 查询完整菜单树（管理用） */
  List<MenuTreeVO> listMenuTree();

  /** 查询当前用户菜单树（按权限过滤） */
  List<MenuTreeVO> listMyMenuTree();

  /** 创建菜单 */
  Long createMenu(MenuCreateDTO dto);

  /** 更新菜单 */
  void updateMenu(Long menuId, MenuUpdateDTO dto);

  /** 删除菜单（逻辑删除） */
  void deleteMenu(Long menuId);
}
