/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.service.impl;

import com.atlas.auth.config.JwtConfig;
import com.atlas.auth.constant.AuthErrorCode;
import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.model.vo.LoginRequestVO;
import com.atlas.auth.model.vo.LoginResponseVO;
import com.atlas.auth.model.vo.UserVO;
import com.atlas.auth.service.AuthService;
import com.atlas.auth.service.SessionService;
import com.atlas.auth.service.TokenService;
import com.atlas.auth.util.PasswordUtil;
import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.exception.DataException;
import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.feign.PermissionQueryApi;
import com.atlas.system.api.v1.feign.UserQueryApi;
import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.enums.UserStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 *
 * <p>实现用户登录和登出功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

  private final UserQueryApi userQueryApi;
  private final PermissionQueryApi permissionQueryApi;
  private final PasswordUtil passwordUtil;
  private final TokenService tokenService;
  private final SessionService sessionService;
  private final JwtConfig jwtConfig;

  public AuthServiceImpl(
      UserQueryApi userQueryApi,
      PermissionQueryApi permissionQueryApi,
      PasswordUtil passwordUtil,
      TokenService tokenService,
      SessionService sessionService,
      JwtConfig jwtConfig) {
    this.userQueryApi = userQueryApi;
    this.permissionQueryApi = permissionQueryApi;
    this.passwordUtil = passwordUtil;
    this.tokenService = tokenService;
    this.sessionService = sessionService;
    this.jwtConfig = jwtConfig;
  }

  @Override
  public LoginResponseVO login(LoginRequestVO loginRequest) {
    // 1. 参数校验
    if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
      throw new BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD_EMPTY, "用户名不能为空");
    }
    if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
      throw new BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD_EMPTY, "密码不能为空");
    }

    // 2. 查询用户信息
    Result<UserDTO> userResult = userQueryApi.getUserByUsername(loginRequest.getUsername());
    if (userResult == null || !userResult.isSuccess() || userResult.getData() == null) {
      log.warn("用户不存在: username={}", loginRequest.getUsername());
      throw new BusinessException(AuthErrorCode.USER_NOT_FOUND, "用户名或密码错误");
    }

    UserDTO userDTO = userResult.getData();

    // 3. 验证用户状态
    if (userDTO.getStatus() == null) {
      log.warn("用户状态为空: userId={}", userDTO.getUserId());
      throw new BusinessException(AuthErrorCode.USER_NOT_FOUND, "用户名或密码错误");
    }

    if (userDTO.getStatus() == UserStatus.INACTIVE) {
      log.warn("用户未激活: userId={}", userDTO.getUserId());
      throw new BusinessException(AuthErrorCode.USER_NOT_ACTIVE, "用户未激活，请联系管理员");
    }

    if (userDTO.getStatus() == UserStatus.LOCKED) {
      log.warn("用户已锁定: userId={}", userDTO.getUserId());
      throw new BusinessException(AuthErrorCode.USER_LOCKED, "用户已锁定，请联系管理员");
    }

    if (userDTO.getStatus() == UserStatus.DELETED) {
      log.warn("用户已删除: userId={}", userDTO.getUserId());
      throw new BusinessException(AuthErrorCode.USER_DELETED, "用户名或密码错误");
    }

    if (userDTO.getStatus() != UserStatus.ACTIVE) {
      log.warn("用户状态异常: userId={}, status={}", userDTO.getUserId(), userDTO.getStatus());
      throw new BusinessException(AuthErrorCode.USER_NOT_ACTIVE, "用户状态异常，请联系管理员");
    }

    // 4. 验证密码
    // 注意：这里假设 UserDTO 中有 password 字段，实际实现中可能需要通过其他方式获取密码
    // 或者需要在 atlas-system 服务中提供密码验证接口
    // TODO: 根据实际实现调整密码验证逻辑
    String storedPassword = getStoredPassword(userDTO);
    if (storedPassword == null || !passwordUtil.matches(loginRequest.getPassword(), storedPassword)) {
      log.warn("密码错误: userId={}", userDTO.getUserId());
      throw new BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD_ERROR, "用户名或密码错误");
    }

    // 5. 查询用户权限和角色
    Result<UserAuthoritiesDTO> authoritiesResult = permissionQueryApi.getUserAuthorities(userDTO.getUserId());
    if (authoritiesResult == null || !authoritiesResult.isSuccess() || authoritiesResult.getData() == null) {
      log.warn("查询用户权限失败: userId={}", userDTO.getUserId());
      // 权限查询失败时，使用空列表
      authoritiesResult = Result.success(new UserAuthoritiesDTO(userDTO.getUserId(), new ArrayList<>(), new ArrayList<>()));
    }

    UserAuthoritiesDTO authoritiesDTO = authoritiesResult.getData();
    List<String> roles = authoritiesDTO.getRoles() != null ? authoritiesDTO.getRoles() : new ArrayList<>();
    List<String> permissions = authoritiesDTO.getPermissions() != null ? authoritiesDTO.getPermissions() : new ArrayList<>();

    // 6. 生成 Token
    TokenInfoDTO tokenInfo = new TokenInfoDTO();
    tokenInfo.setUserId(userDTO.getUserId());
    tokenInfo.setUsername(userDTO.getUsername());
    tokenInfo.setRoles(roles);
    tokenInfo.setPermissions(permissions);

    String token = tokenService.generateToken(tokenInfo);

    // 设置 tokenId（从生成的 Token 中解析）
    TokenInfoDTO parsedTokenInfo = tokenService.parseToken(token);
    tokenInfo.setTokenId(parsedTokenInfo.getTokenId());
    tokenInfo.setIssuedAt(parsedTokenInfo.getIssuedAt());
    tokenInfo.setExpiresAt(parsedTokenInfo.getExpiresAt());

    // 7. 存储会话信息
    sessionService.saveSession(userDTO.getUserId(), tokenInfo, jwtConfig.getExpire());

    // 8. 构建响应
    LoginResponseVO response = new LoginResponseVO();
    response.setToken(token);
    response.setTokenType("Bearer");
    response.setExpiresIn(jwtConfig.getExpire());

    UserVO userVO = new UserVO();
    userVO.setUserId(userDTO.getUserId());
    userVO.setUsername(userDTO.getUsername());
    userVO.setNickname(userDTO.getNickname());
    userVO.setEmail(userDTO.getEmail());
    response.setUser(userVO);

    log.info("用户登录成功: userId={}, username={}", userDTO.getUserId(), userDTO.getUsername());
    return response;
  }

  @Override
  public void logout(String token) {
    if (token == null || token.trim().isEmpty()) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING, "Token 不能为空");
    }

    // 1. 解析 Token
    TokenInfoDTO tokenInfo;
    try {
      tokenInfo = tokenService.parseToken(token);
    } catch (Exception e) {
      log.warn("解析 Token 失败: {}", e.getMessage());
      throw new BusinessException(AuthErrorCode.TOKEN_INVALID, "Token 无效");
    }

    // 2. 验证 Token 有效性
    TokenInfoDTO validatedTokenInfo = tokenService.validateToken(token);
    if (validatedTokenInfo == null) {
      log.warn("Token 验证失败: tokenId={}", tokenInfo.getTokenId());
      throw new BusinessException(AuthErrorCode.TOKEN_INVALID, "Token 无效");
    }

    // 3. 将 Token 加入黑名单
    sessionService.addToBlacklist(tokenInfo.getTokenId(), tokenInfo.getUserId(), jwtConfig.getExpire());

    // 4. 删除会话信息
    sessionService.deleteSession(tokenInfo.getUserId());

    log.info("用户登出成功: userId={}, tokenId={}", tokenInfo.getUserId(), tokenInfo.getTokenId());
  }

  /**
   * 获取存储的密码
   *
   * <p>从 UserDTO 中获取密码。注意：实际实现中，密码不应该在 DTO 中传递。
   * 这里是一个临时实现，实际应该通过其他方式获取密码（如专门的密码验证接口）。
   *
   * @param userDTO 用户 DTO
   * @return 加密后的密码
   */
  private String getStoredPassword(UserDTO userDTO) {
    // TODO: 根据实际实现调整
    // 方案 1: UserDTO 中添加 password 字段（不推荐，不符合安全最佳实践）
    // 方案 2: 在 atlas-system 服务中提供密码验证接口
    // 方案 3: 通过其他方式获取密码
    // 这里暂时返回 null，实际实现中需要根据具体情况调整
    return null;
  }
}

