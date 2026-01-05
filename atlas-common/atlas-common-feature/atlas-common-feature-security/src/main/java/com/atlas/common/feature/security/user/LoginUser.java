/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.user;

import java.util.List;

/**
 * 登录用户信息模型接口
 *
 * <p>该接口定义了登录用户的基本信息和权限信息，为业务模块提供统一的用户身份抽象。
 * 业务模块可以实现此接口并添加自定义字段，以满足特定的业务需求。
 *
 * <p>实现类应该确保：
 * <ul>
 *   <li>getUserId() 不能返回 null</li>
 *   <li>getUsername() 不能返回 null 或空字符串</li>
 *   <li>getRoles() 和 getPermissions() 不能返回 null（可以为空列表）</li>
 *   <li>hasRole() 和 hasPermission() 方法应该基于 getRoles() 和 getPermissions() 的结果进行判断</li>
 * </ul>
 *
 * @author Atlas
 * @since 1.0.0
 */
public interface LoginUser {

  /**
   * 获取用户ID
   *
   * <p>用户ID可以是 String、Long 等任意类型，由实现类决定具体的类型。
   *
   * @return 用户ID，不能为 null
   */
  Object getUserId();

  /**
   * 获取用户名
   *
   * @return 用户名，不能为 null 或空字符串
   */
  String getUsername();

  /**
   * 获取用户角色列表
   *
   * @return 用户角色列表，不能为 null（可以为空列表）
   */
  List<String> getRoles();

  /**
   * 获取用户权限列表
   *
   * @return 用户权限列表，不能为 null（可以为空列表）
   */
  List<String> getPermissions();

  /**
   * 判断是否拥有指定角色
   *
   * <p>该方法应该基于 getRoles() 的结果进行判断。
   *
   * @param role 角色名称
   * @return 如果用户拥有指定角色返回 true，否则返回 false
   */
  boolean hasRole(String role);

  /**
   * 判断是否拥有指定权限
   *
   * <p>该方法应该基于 getPermissions() 的结果进行判断。
   *
   * @param permission 权限名称
   * @return 如果用户拥有指定权限返回 true，否则返回 false
   */
  boolean hasPermission(String permission);
}

