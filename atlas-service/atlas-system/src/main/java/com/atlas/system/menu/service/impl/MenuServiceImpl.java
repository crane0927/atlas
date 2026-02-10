/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.menu.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.security.context.SecurityContextHolder;
import com.atlas.common.feature.security.user.LoginUser;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.menu.mapper.MenuMapper;
import com.atlas.system.menu.model.dto.MenuCreateDTO;
import com.atlas.system.menu.model.dto.MenuUpdateDTO;
import com.atlas.system.menu.model.entity.Menu;
import com.atlas.system.menu.model.vo.MenuTreeVO;
import com.atlas.system.menu.service.MenuService;
import com.atlas.system.permission.mapper.PermissionMapper;
import com.atlas.system.permission.model.entity.Permission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 菜单服务实现类
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

  private static final Set<String> MENU_TYPES = Set.of("DIR", "MENU", "BUTTON");

  private final MenuMapper menuMapper;
  private final PermissionMapper permissionMapper;

  @Override
  public List<MenuTreeVO> listMenuTree() {
    List<Menu> menus = listActiveMenus();
    return buildMenuTree(menus);
  }

  @Override
  public List<MenuTreeVO> listMyMenuTree() {
    LoginUser loginUser = SecurityContextHolder.getLoginUser();
    if (loginUser == null) {
      return Collections.emptyList();
    }
    List<String> permissions = Optional.ofNullable(loginUser.getPermissions())
        .orElse(Collections.emptyList());
    List<Menu> menus = listActiveMenus();
    List<Menu> filtered =
        menus.stream().filter(menu -> isMenuVisible(menu, permissions)).collect(Collectors.toList());
    return buildMenuTree(filtered);
  }

  @Override
  public MenuTreeVO getMenuById(String menuId) {
    Menu menu = menuMapper.selectById(menuId);
    if (menu == null || "DELETED".equals(menu.getStatus())) {
      throw new BusinessException(SystemErrorCode.MENU_NOT_FOUND, "菜单不存在");
    }
    MenuTreeVO vo = new MenuTreeVO();
    BeanUtils.copyProperties(menu, vo);
    vo.setChildren(new ArrayList<>());
    return vo;
  }

  @Override
  @Transactional
  public String createMenu(MenuCreateDTO dto) {
    validateMenuType(dto.getType());
    validateMenuPath(dto.getType(), dto.getPath(), dto.getComponent());
    validatePermissionCode(dto.getPermissionCode());

    String parentId = normalizeParentId(dto.getParentId());
    if (parentId != null) {
      Menu parent = menuMapper.selectById(parentId);
      if (parent == null || "DELETED".equals(parent.getStatus())) {
        throw new BusinessException(SystemErrorCode.MENU_PARENT_NOT_FOUND, "父菜单不存在");
      }
    }

    Menu menu = new Menu();
    menu.setParentId(parentId);
    menu.setName(dto.getName());
    menu.setPath(dto.getPath());
    menu.setComponent(dto.getComponent());
    menu.setRedirect(dto.getRedirect());
    menu.setIcon(dto.getIcon());
    menu.setType(dto.getType());
    menu.setSort(dto.getSort() != null ? dto.getSort() : 0);
    menu.setVisible(dto.getVisible() != null ? dto.getVisible() : Boolean.TRUE);
    menu.setKeepAlive(dto.getKeepAlive() != null ? dto.getKeepAlive() : Boolean.FALSE);
    menu.setExternal(dto.getExternal() != null ? dto.getExternal() : Boolean.FALSE);
    menu.setPermissionCode(dto.getPermissionCode());
    menu.setStatus("ACTIVE");

    menuMapper.insert(menu);
    return menu.getMenuId();
  }

  @Override
  @Transactional
  public void updateMenu(String menuId, MenuUpdateDTO dto) {
    Menu existing = menuMapper.selectById(menuId);
    if (existing == null || "DELETED".equals(existing.getStatus())) {
      throw new BusinessException(SystemErrorCode.MENU_NOT_FOUND, "菜单不存在");
    }
    validateMenuType(dto.getType());
    validateMenuPath(dto.getType(), dto.getPath(), dto.getComponent());
    validatePermissionCode(dto.getPermissionCode());

    String parentId = normalizeParentId(dto.getParentId());
    if (parentId != null) {
      if (menuId.equals(parentId)) {
        throw new BusinessException(SystemErrorCode.MENU_PARENT_INVALID, "父菜单不能是自身");
      }
      Menu parent = menuMapper.selectById(parentId);
      if (parent == null || "DELETED".equals(parent.getStatus())) {
        throw new BusinessException(SystemErrorCode.MENU_PARENT_NOT_FOUND, "父菜单不存在");
      }
    }

    existing.setParentId(parentId);
    existing.setName(dto.getName());
    existing.setPath(dto.getPath());
    existing.setComponent(dto.getComponent());
    existing.setRedirect(dto.getRedirect());
    existing.setIcon(dto.getIcon());
    existing.setType(dto.getType());
    existing.setSort(dto.getSort() != null ? dto.getSort() : 0);
    existing.setVisible(dto.getVisible() != null ? dto.getVisible() : Boolean.TRUE);
    existing.setKeepAlive(dto.getKeepAlive() != null ? dto.getKeepAlive() : Boolean.FALSE);
    existing.setExternal(dto.getExternal() != null ? dto.getExternal() : Boolean.FALSE);
    existing.setPermissionCode(dto.getPermissionCode());
    if (StringUtils.hasText(dto.getStatus())) {
      existing.setStatus(dto.getStatus());
    }
    menuMapper.updateById(existing);
  }

  @Override
  @Transactional
  public void deleteMenu(String menuId) {
    Menu existing = menuMapper.selectById(menuId);
    if (existing == null || "DELETED".equals(existing.getStatus())) {
      throw new BusinessException(SystemErrorCode.MENU_NOT_FOUND, "菜单不存在");
    }
    Menu update = new Menu();
    update.setMenuId(menuId);
    update.setStatus("DELETED");
    update.setDeleted(1);
    menuMapper.updateById(update);
  }

  private List<Menu> listActiveMenus() {
    LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
    wrapper.ne(Menu::getStatus, "DELETED");
    wrapper.orderByAsc(Menu::getSort).orderByAsc(Menu::getMenuId);
    return menuMapper.selectList(wrapper);
  }

  private boolean isMenuVisible(Menu menu, List<String> permissions) {
    if (!StringUtils.hasText(menu.getPermissionCode())) {
      return true;
    }
    return permissions.contains(menu.getPermissionCode());
  }

  private void validateMenuType(String type) {
    if (!StringUtils.hasText(type) || !MENU_TYPES.contains(type)) {
      throw new BusinessException(SystemErrorCode.MENU_TYPE_INVALID, "菜单类型不合法");
    }
  }

  private void validateMenuPath(String type, String path, String component) {
    if (("DIR".equals(type) || "MENU".equals(type)) && !StringUtils.hasText(path)) {
      throw new BusinessException(SystemErrorCode.MENU_PATH_REQUIRED, "目录/菜单必须填写路由路径");
    }
    if ("MENU".equals(type) && !StringUtils.hasText(component)) {
      throw new BusinessException(SystemErrorCode.MENU_COMPONENT_REQUIRED, "菜单必须填写组件标识");
    }
  }

  private void validatePermissionCode(String permissionCode) {
    if (!StringUtils.hasText(permissionCode)) {
      return;
    }
    Permission permission =
        permissionMapper.selectOne(
            new LambdaQueryWrapper<Permission>()
                .eq(Permission::getPermissionCode, permissionCode)
                .ne(Permission::getStatus, "DELETED"));
    if (permission == null) {
      throw new BusinessException(SystemErrorCode.MENU_PERMISSION_NOT_FOUND, "权限码不存在");
    }
  }

  private String normalizeParentId(String parentId) {
    if (parentId == null || "0".equals(parentId) || parentId.isEmpty()) {
      return null;
    }
    return parentId;
  }

  private List<MenuTreeVO> buildMenuTree(List<Menu> menus) {
    Map<String, MenuTreeVO> map = new HashMap<>();
    for (Menu menu : menus) {
      MenuTreeVO vo = new MenuTreeVO();
      BeanUtils.copyProperties(menu, vo);
      map.put(menu.getMenuId(), vo);
    }

    List<MenuTreeVO> roots = new ArrayList<>();
    for (Menu menu : menus) {
      MenuTreeVO vo = map.get(menu.getMenuId());
      String parentId = normalizeParentId(menu.getParentId());
      if (parentId == null || !map.containsKey(parentId)) {
        roots.add(vo);
      } else {
        map.get(parentId).getChildren().add(vo);
      }
    }

    Comparator<MenuTreeVO> comparator =
        Comparator.comparing(MenuTreeVO::getSort, Comparator.nullsLast(Integer::compareTo))
            .thenComparing(MenuTreeVO::getMenuId, Comparator.nullsLast(String::compareTo));
    sortTree(roots, comparator);
    return roots;
  }

  private void sortTree(List<MenuTreeVO> nodes, Comparator<MenuTreeVO> comparator) {
    nodes.sort(comparator);
    for (MenuTreeVO node : nodes) {
      if (node.getChildren() != null && !node.getChildren().isEmpty()) {
        sortTree(node.getChildren(), comparator);
      }
    }
  }
}
