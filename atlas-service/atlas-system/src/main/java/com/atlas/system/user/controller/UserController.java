/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.controller;

import com.atlas.common.feature.core.page.PageResult;
import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.feign.UserQueryApi;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.user.model.dto.UserQueryDTO;
import com.atlas.system.user.model.vo.UserListVO;
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
 *   <li>GET /api/v1/users：分页查询用户列表（参数：page、size、sort、username、status）
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
   * 分页查询用户列表
   *
   * <p>支持按用户名模糊、状态筛选，以及排序（排序字段：createTime、username）。{@link UserQueryDTO} 继承 {@link
   * com.atlas.common.feature.core.page.PageQueryDTO}，含 page、size、sort。
   *
   * @param query 查询条件（username、status、page、size、sort）
   * @return 分页结果，使用 {@link Result} 包装
   */
  @GetMapping("/api/v1/users")
  public Result<PageResult<UserListVO>> listUsers(UserQueryDTO query) {
    PageResult<UserListVO> result = userService.listUsersPage(query);
    return Result.success(result);
  }

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

  /**
   * 验证用户密码
   *
   * <p>通过用户名和密码验证用户身份，用于登录场景。
   *
   * @param username 用户名
   * @param password 明文密码
   * @return 验证结果，使用 {@link Result} 包装，data 为加密后的密码（用于后续验证）
   */
  @Override
  @GetMapping("/api/v1/users/verify-password")
  public Result<String> verifyPassword(
      @RequestParam String username, @RequestParam String password) {
    String encodedPassword = userService.verifyPassword(username, password);
    return Result.success(encodedPassword);
  }
}
