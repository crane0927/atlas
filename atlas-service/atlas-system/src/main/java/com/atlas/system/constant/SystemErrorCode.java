/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.constant;

/**
 * 系统域错误码常量类
 *
 * <p>定义系统域（atlas-system）的错误码，模块码使用 03。
 *
 * <p>错误码格式：03XYZZ（6 位数字）
 *
 * <ul>
 *   <li>03: 模块码（atlas-system）
 *   <li>XY: 类型码（20=用户、21=角色、22=权限、23=系统设置）
 *   <li>ZZ: 序号（01-99）
 * </ul>
 *
 * <p>示例：032001 = 用户不存在，032101 = 角色不存在，032201 = 权限不存在，032301 = 设置项不存在。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class SystemErrorCode {

  // ========== 用户相关错误 (03 20 xx) ==========

  /** 用户不存在 */
  public static final String USER_NOT_FOUND = "032001";

  /** 用户名已存在 */
  public static final String USERNAME_ALREADY_EXISTS = "032004";

  // 预留错误码：032002-032003, 032005-032099

  // ========== 角色相关错误 (03 21 xx) ==========

  /** 角色不存在 */
  public static final String ROLE_NOT_FOUND = "032101";

  /** 角色代码已存在 */
  public static final String ROLE_CODE_ALREADY_EXISTS = "032105";

  // 预留错误码：032102-032104, 032106-032199

  // ========== 权限相关错误 (03 22 xx) ==========

  /** 权限不存在 */
  public static final String PERMISSION_NOT_FOUND = "032201";

  /** 权限代码已存在 */
  public static final String PERMISSION_CODE_ALREADY_EXISTS = "032206";

  // 预留错误码：032202-032205, 032207-032299

  // ========== 系统设置相关错误 (03 23 xx) ==========

  /** 设置项不存在 */
  public static final String SYSTEM_SETTING_NOT_FOUND = "032301";

  /** 设置项 key 已存在 */
  public static final String SYSTEM_SETTING_KEY_ALREADY_EXISTS = "032302";

  /** 系统类型设置项不可删除 */
  public static final String SYSTEM_SETTING_SYSTEM_DELETE_FORBIDDEN = "032303";

  /** 设置项 value 不合法 */
  public static final String SYSTEM_SETTING_VALUE_INVALID = "032304";

  // 预留错误码：032305-032399

  // ========== 菜单相关错误 (03 24 xx) ==========

  /** 菜单不存在 */
  public static final String MENU_NOT_FOUND = "032401";

  /** 父菜单不存在 */
  public static final String MENU_PARENT_NOT_FOUND = "032402";

  /** 父菜单非法 */
  public static final String MENU_PARENT_INVALID = "032403";

  /** 菜单类型不合法 */
  public static final String MENU_TYPE_INVALID = "032404";

  /** 目录/菜单必须填写路由路径 */
  public static final String MENU_PATH_REQUIRED = "032405";

  /** 菜单必须填写组件标识 */
  public static final String MENU_COMPONENT_REQUIRED = "032406";

  /** 权限码不存在 */
  public static final String MENU_PERMISSION_NOT_FOUND = "032407";

  /** 私有构造函数，防止实例化 */
  private SystemErrorCode() {
    throw new UnsupportedOperationException("常量类不允许实例化");
  }
}
