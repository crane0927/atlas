/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.model.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户列表项 VO
 *
 * <p>用于用户分页列表接口的返回项，不包含密码等敏感字段。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>userId：用户ID
 *   <li>username：用户名
 *   <li>nickname：昵称
 *   <li>email：邮箱
 *   <li>phone：手机号
 *   <li>status：用户状态
 *   <li>avatar：头像URL
 *   <li>createdAt：创建时间
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class UserListVO {

  /** 用户ID */
  private String userId;

  /** 用户名 */
  private String username;

  /** 昵称 */
  private String nickname;

  /** 邮箱 */
  private String email;

  /** 手机号 */
  private String phone;

  /** 用户状态 */
  private String status;

  /** 头像URL */
  private String avatar;

  /** 创建时间 */
  private LocalDateTime createdAt;
}
