/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.constant;

/**
 * 系统域错误码常量类
 *
 * <p>定义系统域（atlas-system）的错误码，模块码使用 03。
 *
 * <p>错误码格式：MMTTSS（6位数字）
 * <ul>
 *   <li>MM: 模块码（03）
 *   <li>TT: 错误类型码（00-99）
 *   <li>SS: 序号（00-99）
 * </ul>
 *
 * <p>错误类型码分配：
 * <ul>
 *   <li>00-09: 用户相关错误
 *   <li>10-19: 角色相关错误
 *   <li>20-29: 权限相关错误
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class SystemErrorCode {

  // ========== 用户相关错误 (00-09) ==========

  /** 用户不存在 */
  public static final String USER_NOT_FOUND = "032001";

  /** 用户名已存在 */
  public static final String USERNAME_ALREADY_EXISTS = "032004";

  // 预留错误码：032002-032003, 032005-032099

  // ========== 角色相关错误 (10-19) ==========

  /** 角色不存在 */
  public static final String ROLE_NOT_FOUND = "032101";

  /** 角色代码已存在 */
  public static final String ROLE_CODE_ALREADY_EXISTS = "032105";

  // 预留错误码：032102-032104, 032106-032199

  // ========== 权限相关错误 (20-29) ==========

  /** 权限不存在 */
  public static final String PERMISSION_NOT_FOUND = "032201";

  /** 权限代码已存在 */
  public static final String PERMISSION_CODE_ALREADY_EXISTS = "032206";

  // 预留错误码：032202-032205, 032207-032299

  /** 私有构造函数，防止实例化 */
  private SystemErrorCode() {
    throw new UnsupportedOperationException("常量类不允许实例化");
  }
}
