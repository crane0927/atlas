/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.controller;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.feign.UserQueryApi;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户查询控制器
 *
 * <p>实现 UserQueryApi 接口，提供用户查询的 RESTful API 接口。
 *
 * <p>接口说明：
 *
 * <ul>
 *   <li>GET /api/v1/users/{userId}：根据用户ID查询用户信息
 *   <li>GET /api/v1/users/by-username?username={username}：根据用户名查询用户信息
 * </ul>
 *
 * <p>返回格式：统一使用 {@link Result} 包装响应数据
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UserQueryApi {

  private final UserService userService;

  /**
   * 根据用户ID查询用户信息
   *
   * <p>通过用户ID查询用户的基本信息，包括用户ID、用户名、状态等。
   *
   * @param userId 用户ID
   * @return 用户信息，使用 {@link Result} 包装
   */
  @Override
  @GetMapping("/api/v1/users/{userId}")
  public Result<UserDTO> getUserById(@PathVariable Long userId) {
    UserDTO userDTO = userService.getUserById(userId);
    return Result.success(userDTO);
  }

  /**
   * 根据用户名查询用户信息
   *
   * <p>通过用户名查询用户的基本信息，包括用户ID、用户名、状态等。
   *
   * @param username 用户名
   * @return 用户信息，使用 {@link Result} 包装
   */
  @Override
  @GetMapping("/api/v1/users/by-username")
  public Result<UserDTO> getUserByUsername(@RequestParam String username) {
    UserDTO userDTO = userService.getUserByUsername(username);
    return Result.success(userDTO);
  }
}
