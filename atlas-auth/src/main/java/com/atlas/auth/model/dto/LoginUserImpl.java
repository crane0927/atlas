/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.model.dto;

import com.atlas.common.feature.security.user.LoginUser;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginUser 实现类
 *
 * <p>实现 `LoginUser` 接口，封装用户信息供下游服务使用。
 *
 * <p>字段说明：
 * <ul>
 *   <li>userId：用户ID</li>
 *   <li>username：用户名</li>
 *   <li>roles：角色列表（不能为 null）</li>
 *   <li>permissions：权限列表（不能为 null）</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserImpl implements LoginUser {

  /** 用户ID */
  private Long userId;

  /** 用户名 */
  private String username;

  /** 角色列表（不能为 null） */
  private List<String> roles;

  /** 权限列表（不能为 null） */
  private List<String> permissions;

  @Override
  public Object getUserId() {
    return userId;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public List<String> getRoles() {
    return roles != null ? roles : Collections.emptyList();
  }

  @Override
  public List<String> getPermissions() {
    return permissions != null ? permissions : Collections.emptyList();
  }

  @Override
  public boolean hasRole(String role) {
    return getRoles().contains(role);
  }

  @Override
  public boolean hasPermission(String permission) {
    return getPermissions().contains(permission);
  }
}

