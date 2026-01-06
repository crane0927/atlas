/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.api.v1.feign;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.model.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户查询接口
 *
 * <p>定义用户查询的 Feign 接口，供 auth 服务和其他服务查询用户信息。
 *
 * <p>接口说明：
 * <ul>
 *   <li>getUserById：根据用户ID查询用户信息</li>
 *   <li>getUserByUsername：根据用户名查询用户信息</li>
 * </ul>
 *
 * <p>服务名称：atlas-system
 *
 * <p>基础路径：/api/v1
 *
 * <p>返回格式：统一使用 {@link Result} 包装响应数据
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@FeignClient(name = "atlas-system", path = "/api/v1")
public interface UserQueryApi {

  /**
   * 根据用户ID查询用户信息
   *
   * <p>通过用户ID查询用户的基本信息，包括用户ID、用户名、状态等。
   *
   * @param userId 用户ID
   * @return 用户信息，使用 {@link Result} 包装
   */
  @GetMapping("/users/{userId}")
  Result<UserDTO> getUserById(@PathVariable Long userId);

  /**
   * 根据用户名查询用户信息
   *
   * <p>通过用户名查询用户的基本信息，包括用户ID、用户名、状态等。
   *
   * @param username 用户名
   * @return 用户信息，使用 {@link Result} 包装
   */
  @GetMapping("/users/by-username")
  Result<UserDTO> getUserByUsername(@RequestParam String username);
}

