/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.service;

import com.atlas.auth.model.vo.LoginRequestVO;
import com.atlas.auth.model.vo.LoginResponseVO;

/**
 * 认证服务接口
 *
 * <p>提供用户登录和登出功能。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>用户登录（验证用户身份并签发 Token）
 *   <li>用户登出（使 Token 失效并清除会话）
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface AuthService {

  /**
   * 用户登录
   *
   * <p>验证用户身份并签发 Token。
   *
   * <p>流程：
   * <ol>
   *   <li>通过 `atlas-system-api` 查询用户信息
   *   <li>验证用户状态（必须为激活状态）
   *   <li>验证用户密码
   *   <li>通过 `atlas-system-api` 查询用户权限和角色
   *   <li>生成 JWT Token
   *   <li>存储用户会话信息到 Redis
   *   <li>返回 Token 和用户基本信息
   * </ol>
   *
   * @param loginRequest 登录请求（用户名、密码）
   * @return 登录响应（Token、用户信息）
   * @throws com.atlas.common.feature.core.exception.BusinessException 如果登录失败（用户名或密码错误、用户未激活等）
   */
  LoginResponseVO login(LoginRequestVO loginRequest);

  /**
   * 用户登出
   *
   * <p>使 Token 失效并清除会话。
   *
   * <p>流程：
   * <ol>
   *   <li>从请求头提取 Token
   *   <li>解析 Token 获取用户ID和 Token ID
   *   <li>验证 Token 有效性
   *   <li>将 Token 加入黑名单
   *   <li>清除用户会话信息
   * </ol>
   *
   * @param token JWT Token 字符串
   * @throws com.atlas.common.feature.core.exception.BusinessException 如果 Token 无效
   */
  void logout(String token);
}

