/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.role.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色更新 DTO
 *
 * <p>用于更新角色的请求数据传输对象。不修改 roleCode（角色代码不可改）。
 *
 * <ul>
 *   <li>roleName：角色名称，可选
 *   <li>description：角色描述，可选
 *   <li>status：角色状态，可选（ACTIVE/INACTIVE/DELETED）
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class RoleUpdateDTO {

  /** 角色名称，可选 */
  @Size(max = 100, message = "角色名称长度不能超过 100 个字符")
  private String roleName;

  /** 角色描述，可选 */
  @Size(max = 500, message = "角色描述长度不能超过 500 个字符")
  private String description;

  /** 角色状态，可选（ACTIVE/INACTIVE/DELETED） */
  @Size(max = 20, message = "状态长度不能超过 20 个字符")
  private String status;
}
