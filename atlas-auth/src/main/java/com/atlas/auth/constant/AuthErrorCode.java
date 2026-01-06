/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.constant;

/**
 * Auth 模块错误码常量类
 *
 * <p>定义 Auth 模块的错误码，模块码使用 01（atlas-auth）。
 *
 * <p>错误码格式：MMTTSS（6位数字）
 * <ul>
 *   <li>MM: 模块码（01）</li>
 *   <li>TT: 错误类型码（00-99）</li>
 *   <li>SS: 序号（00-99）</li>
 * </ul>
 *
 * <p>错误类型码分配：
 * <ul>
 *   <li>00-09: 系统错误</li>
 *   <li>10-19: 参数错误</li>
 *   <li>20-29: 业务错误（登录失败、用户状态等）</li>
 *   <li>30-39: 权限错误（Token 相关）</li>
 *   <li>40-49: 数据错误</li>
 *   <li>50-99: 预留扩展</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class AuthErrorCode {

  // ========== 系统错误 (00-09) ==========

  /** 系统服务不可用 */
  public static final String SYSTEM_SERVICE_UNAVAILABLE = "010001";

  // ========== 参数错误 (10-19) ==========

  /** 用户名或密码不能为空 */
  public static final String USERNAME_OR_PASSWORD_EMPTY = "011000";

  // ========== 业务错误 (20-29) ==========

  /** 用户名或密码错误 */
  public static final String USERNAME_OR_PASSWORD_ERROR = "012000";

  /** 用户不存在 */
  public static final String USER_NOT_FOUND = "012001";

  /** 用户未激活 */
  public static final String USER_NOT_ACTIVE = "012002";

  /** 用户已锁定 */
  public static final String USER_LOCKED = "012003";

  /** 用户已删除 */
  public static final String USER_DELETED = "012004";

  // ========== 权限错误 (30-39) ==========

  /** Token 无效 */
  public static final String TOKEN_INVALID = "013000";

  /** Token 已过期 */
  public static final String TOKEN_EXPIRED = "013001";

  /** Token 签名无效 */
  public static final String TOKEN_SIGNATURE_INVALID = "013002";

  /** Token 已失效（黑名单） */
  public static final String TOKEN_BLACKLISTED = "013003";

  /** Token 缺失 */
  public static final String TOKEN_MISSING = "013004";

  /** 私有构造函数，防止实例化 */
  private AuthErrorCode() {
    throw new UnsupportedOperationException("常量类不允许实例化");
  }
}

