/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.auth.service.impl;

import com.atlas.auth.config.AuthProperties;
import com.atlas.auth.config.JwtConfig;
import com.atlas.auth.constant.AuthErrorCode;
import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.model.vo.LoginRequestVO;
import com.atlas.auth.model.vo.LoginResponseVO;
import com.atlas.auth.model.vo.UserVO;
import com.atlas.auth.service.AuthService;
import com.atlas.auth.service.CaptchaService;
import com.atlas.auth.service.SessionService;
import com.atlas.auth.service.TokenService;
import com.atlas.auth.util.RsaPasswordDecryptor;
import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.feign.PermissionQueryApi;
import com.atlas.system.api.v1.feign.UserQueryApi;
import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.dto.VerifyPasswordRequest;
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
  private final TokenService tokenService;
  private final SessionService sessionService;
  private final JwtConfig jwtConfig;
  private final AuthProperties authProperties;
  private final RsaPasswordDecryptor rsaPasswordDecryptor;
  private final CaptchaService captchaService;

  public AuthServiceImpl(
      UserQueryApi userQueryApi,
      PermissionQueryApi permissionQueryApi,
      TokenService tokenService,
      SessionService sessionService,
      JwtConfig jwtConfig,
      AuthProperties authProperties,
      RsaPasswordDecryptor rsaPasswordDecryptor,
      CaptchaService captchaService) {
    this.userQueryApi = userQueryApi;
    this.permissionQueryApi = permissionQueryApi;
    this.tokenService = tokenService;
    this.sessionService = sessionService;
    this.jwtConfig = jwtConfig;
    this.authProperties = authProperties;
    this.rsaPasswordDecryptor = rsaPasswordDecryptor;
    this.captchaService = captchaService;
  }

  @Override
  public LoginResponseVO login(LoginRequestVO loginRequest) {
    // 1. 参数校验
    if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
      throw new BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD_EMPTY, "用户名不能为空");
    }
    if (loginRequest.getEncryptedPassword() == null
        || loginRequest.getEncryptedPassword().trim().isEmpty()) {
      throw new BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD_EMPTY, "密码不能为空");
    }

    // 2. 验证码校验（当启用时）
    if (authProperties.getCaptcha().isEnabled()) {
      if (loginRequest.getCaptchaKey() == null || loginRequest.getCaptchaKey().trim().isEmpty()
          || loginRequest.getCaptchaCode() == null) {
        throw new BusinessException(
            AuthErrorCode.CAPTCHA_EXPIRED_OR_MISSING, "验证码已过期或未填写，请刷新后重试");
      }
      if (!captchaService.validateAndConsume(
          loginRequest.getCaptchaKey(), loginRequest.getCaptchaCode())) {
        throw new BusinessException(AuthErrorCode.CAPTCHA_INVALID, "验证码错误");
      }
    }

    // 3. 解密密码
    String plainPassword = rsaPasswordDecryptor.decrypt(loginRequest.getEncryptedPassword());
    if (plainPassword == null) {
      log.warn("密码解密失败: username={}", loginRequest.getUsername());
      throw new BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD_ERROR, "用户名或密码错误");
    }

    // 4. 查询用户信息
    Result<UserDTO> userResult = userQueryApi.getUserByUsername(loginRequest.getUsername());
    if (userResult == null || !userResult.isSuccess() || userResult.getData() == null) {
      log.warn("用户不存在: username={}", loginRequest.getUsername());
      throw new BusinessException(AuthErrorCode.USER_NOT_FOUND, "用户名或密码错误");
    }

    UserDTO userDTO = userResult.getData();

    // 5. 验证用户状态
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

    // 6. 验证密码（委托 System 的 verifyPassword：成功即表示密码正确，不依赖返回值内容）
    if (!verifyPasswordWithSystem(userDTO.getUsername(), plainPassword)) {
      log.warn("密码错误: userId={}", userDTO.getUserId());
      throw new BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD_ERROR, "用户名或密码错误");
    }

    // 7. 查询用户权限和角色
    Result<UserAuthoritiesDTO> authoritiesResult =
        permissionQueryApi.getUserAuthorities(userDTO.getUserId());
    if (authoritiesResult == null
        || !authoritiesResult.isSuccess()
        || authoritiesResult.getData() == null) {
      log.warn("查询用户权限失败: userId={}", userDTO.getUserId());
      // 权限查询失败时，使用空列表
      authoritiesResult =
          Result.success(
              new UserAuthoritiesDTO(userDTO.getUserId(), new ArrayList<>(), new ArrayList<>()));
    }

    UserAuthoritiesDTO authoritiesDTO = authoritiesResult.getData();
    List<String> roles =
        authoritiesDTO.getRoles() != null ? authoritiesDTO.getRoles() : new ArrayList<>();
    List<String> permissions =
        authoritiesDTO.getPermissions() != null
            ? authoritiesDTO.getPermissions()
            : new ArrayList<>();

    // 8. 生成 Token
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

    // 9. 存储会话信息
    sessionService.saveSession(userDTO.getUserId(), tokenInfo, jwtConfig.getExpire());

    // 10. 构建响应
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
    sessionService.addToBlacklist(
        tokenInfo.getTokenId(), tokenInfo.getUserId(), jwtConfig.getExpire());

    // 4. 删除会话信息
    sessionService.deleteSession(tokenInfo.getUserId());

    log.info("用户登出成功: userId={}, tokenId={}", tokenInfo.getUserId(), tokenInfo.getTokenId());
  }

  /**
   * 委托 System 服务校验密码
   *
   * <p>调用 verifyPassword：成功（Result.isSuccess）即表示密码正确，不依赖返回的 data 内容。 此方法设置为 protected 以便在测试中 mock。
   *
   * @param username 用户名
   * @param password 明文密码
   * @return true 表示密码正确，false 表示错误或调用失败
   */
  protected boolean verifyPasswordWithSystem(String username, String password) {
    if (username == null || password == null) {
      return false;
    }
    try {
      VerifyPasswordRequest request = new VerifyPasswordRequest(username, password);
      Result<String> result = userQueryApi.verifyPassword(request);
      return result != null && result.isSuccess();
    } catch (Exception e) {
      log.warn("密码校验失败: username={}, error={}", username, e.getMessage());
      return false;
    }
  }
}
