/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.role.model.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 角色列表项 VO
 *
 * <p>用于角色分页列表接口的返回项。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>roleId：角色ID
 *   <li>roleCode：角色代码
 *   <li>roleName：角色名称
 *   <li>description：角色描述
 *   <li>status：角色状态
 *   <li>createdAt：创建时间
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class RoleListVO {

  /** 角色ID */
  private Long roleId;

  /** 角色代码 */
  private String roleCode;

  /** 角色名称 */
  private String roleName;

  /** 角色描述 */
  private String description;

  /** 角色状态 */
  private String status;

  /** 创建时间 */
  private LocalDateTime createdAt;
}
