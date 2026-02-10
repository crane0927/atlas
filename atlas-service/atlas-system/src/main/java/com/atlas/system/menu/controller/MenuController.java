/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.menu.controller;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.menu.model.dto.MenuCreateDTO;
import com.atlas.system.menu.model.dto.MenuUpdateDTO;
import com.atlas.system.menu.model.vo.MenuTreeVO;
import com.atlas.system.menu.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单管理控制器
 *
 * <p>提供菜单树查询、创建、更新、删除等 RESTful API。
 *
 * <p>接口说明：
 *
 * <ul>
 *   <li>GET /api/v1/menus/tree：查询完整菜单树（管理用）
 *   <li>GET /api/v1/menus/me：查询当前用户菜单树（按权限过滤）
 *   <li>POST /api/v1/menus：创建菜单
 *   <li>PUT /api/v1/menus/{menuId}：更新菜单
 *   <li>DELETE /api/v1/menus/{menuId}：删除菜单（逻辑删除）
 * </ul>
 *
 * <p>返回格式：统一使用 {@link Result} 包装响应数据
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;

  /** 查询完整菜单树 */
  @GetMapping("/menus/tree")
  public Result<List<MenuTreeVO>> listMenuTree() {
    return Result.success(menuService.listMenuTree());
  }

  /** 查询当前用户菜单树 */
  @GetMapping("/menus/me")
  public Result<List<MenuTreeVO>> listMyMenuTree() {
    return Result.success(menuService.listMyMenuTree());
  }

  /** 创建菜单 */
  @PostMapping("/menus")
  public Result<String> createMenu(@Valid @RequestBody MenuCreateDTO dto) {
    return Result.success(menuService.createMenu(dto));
  }

  /** 更新菜单 */
  @PutMapping("/menus/{menuId}")
  public Result<Void> updateMenu(@PathVariable String menuId, @Valid @RequestBody MenuUpdateDTO dto) {
    menuService.updateMenu(menuId, dto);
    return Result.success(null);
  }

  /** 删除菜单 */
  @DeleteMapping("/menus/{menuId}")
  public Result<Void> deleteMenu(@PathVariable String menuId) {
    menuService.deleteMenu(menuId);
    return Result.success(null);
  }
}
