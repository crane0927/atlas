/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.service;

import com.atlas.system.api.v1.model.dto.UserDTO;

/**
 * 用户服务接口
 *
 * <p>提供用户查询业务逻辑，支持根据用户ID和用户名查询用户信息。
 *
 * <p>方法说明：
 * <ul>
 *   <li>getUserById：根据用户ID查询用户信息
 *   <li>getUserByUsername：根据用户名查询用户信息
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
}
