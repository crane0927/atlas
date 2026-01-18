/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.service;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.user.model.dto.UserCreateDTO;

/**
 * 用户服务接口
 *
 * <p>提供用户查询和创建业务逻辑，支持根据用户ID和用户名查询用户信息，以及创建用户。
 *
 * <p>方法说明：
 *
 * <ul>
 *   <li>getUserById：根据用户ID查询用户信息
 *   <li>getUserByUsername：根据用户名查询用户信息
 *   <li>createUser：创建用户
 *   <li>assignRoleToUser：为用户分配角色
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface UserService {

  /**
   * 根据用户ID查询用户信息
   *
   * <p>通过用户ID查询用户的基本信息，包括用户ID、用户名、状态等。
   *
   * @param userId 用户ID
   * @return 用户信息 DTO，如果用户不存在则抛出 BusinessException
   */
  UserDTO getUserById(Long userId);

  /**
   * 根据用户名查询用户信息
   *
   * <p>通过用户名查询用户的基本信息，包括用户ID、用户名、状态等。
   *
   * @param username 用户名
   * @return 用户信息 DTO，如果用户不存在则抛出 BusinessException
   */
  UserDTO getUserByUsername(String username);

  /**
   * 验证用户密码
   *
   * <p>通过用户名和密码验证用户身份，返回加密后的密码用于后续验证。
   *
   * @param username 用户名
   * @param password 明文密码
   * @return 加密后的密码，如果用户不存在或密码错误则抛出 BusinessException
   */
  String verifyPassword(String username, String password);

  /**
   * 创建用户
   *
   * <p>根据用户创建 DTO 创建新用户，密码会自动加密存储。
   *
   * @param userCreateDTO 用户创建 DTO
   * @return 用户信息 DTO
   * @throws BusinessException 如果用户名已存在，错误码：032004
   */
  UserDTO createUser(UserCreateDTO userCreateDTO);

  /**
   * 为用户分配角色
   *
   * <p>建立用户与角色的关联关系。
   *
   * @param userId 用户ID
   * @param roleId 角色ID
   * @throws BusinessException 如果用户或角色不存在，错误码：032001 或 032101
   */
  void assignRoleToUser(Long userId, Long roleId);
}
