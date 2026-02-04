/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.permission.model.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 权限列表项 VO
 *
 * <p>用于权限分页列表接口的返回项。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>permissionId：权限ID
 *   <li>permissionCode：权限代码
 *   <li>permissionName：权限名称
 *   <li>description：权限描述
 *   <li>status：权限状态
 *   <li>createdAt：创建时间
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class PermissionListVO {

  /** 权限ID */
  private Long permissionId;

  /** 权限代码 */
  private String permissionCode;

  /** 权限名称 */
  private String permissionName;

  /** 权限描述 */
  private String description;

  /** 权限状态 */
  private String status;

  /** 创建时间 */
  private LocalDateTime createdAt;
}
