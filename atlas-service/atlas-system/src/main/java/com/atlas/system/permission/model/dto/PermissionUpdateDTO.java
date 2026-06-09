/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 权限更新 DTO
 *
 * <p>用于更新权限的请求数据传输对象。不修改 permissionCode（权限代码不可改）。
 *
 * <ul>
 *   <li>permissionName：权限名称，可选
 *   <li>description：权限描述，可选
 *   <li>status：权限状态，可选（ACTIVE/INACTIVE/DELETED）
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class PermissionUpdateDTO {

  /** 权限名称，可选 */
  @Size(max = 100, message = "权限名称长度不能超过 100 个字符")
  private String permissionName;

  /** 权限描述，可选 */
  @Size(max = 500, message = "权限描述长度不能超过 500 个字符")
  private String description;

  /** 权限状态，可选（ACTIVE/INACTIVE/DELETED） */
  @Size(max = 20, message = "状态长度不能超过 20 个字符")
  private String status;
}
