/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.security.user;

import java.util.Collections;
import java.util.List;

/**
 * 简单 LoginUser 实现，可从 Gateway 下传的请求头（X-User-Id、X-Username、X-User-Roles）构建。
 *
 * <p>下游服务在 Gateway 已校验并传递用户信息头时使用，避免重复解析 Token。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class SimpleLoginUser implements LoginUser {

  private Object userId;
  private String username;
  private List<String> roles;
  private List<String> permissions;

  public SimpleLoginUser() {}

  public SimpleLoginUser(
      Object userId, String username, List<String> roles, List<String> permissions) {
    this.userId = userId;
    this.username = username;
    this.roles = roles;
    this.permissions = permissions;
  }

  public void setUserId(Object userId) {
    this.userId = userId;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
  }

  @Override
  public Object getUserId() {
    return userId;
  }

  @Override
  public String getUsername() {
    return username != null ? username : "";
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
