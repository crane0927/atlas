/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.api.v1.feign;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.dto.VerifyPasswordRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户查询接口
 *
 * <p>定义用户查询的 Feign 接口，供 auth 服务和其他服务查询用户信息。
 *
 * <p>接口说明：
 *
 * <ul>
 *   <li>getUserById：根据用户ID查询用户信息
 *   <li>getUserByUsername：根据用户名查询用户信息
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
@FeignClient(value = "atlas-system", contextId = "user-query-api", path = "/atlas-system/api/v1")
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
  Result<UserDTO> getUserById(@PathVariable String userId);

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

  /**
   * 验证用户密码
   *
   * <p>通过用户名和密码验证用户身份，用于登录场景。使用 POST 与请求体传递密码，避免出现在 URL/日志中。
   *
   * @param request 包含 username、password 的请求体
   * @return 验证结果，使用 {@link Result} 包装，data 为加密后的密码（用于后续验证）
   */
  @PostMapping("/users/verify-password")
  Result<String> verifyPassword(@RequestBody VerifyPasswordRequest request);
}
