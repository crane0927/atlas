/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 权限创建 DTO
 *
 * <p>用于创建权限的请求数据传输对象。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>permissionCode：权限代码，必填，3-100 个字符
 *   <li>permissionName：权限名称，必填，1-100 个字符
 *   <li>description：权限描述，可选
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class PermissionCreateDTO {

  /** 权限代码，必填，3-100 个字符 */
  @NotBlank(message = "权限代码不能为空")
  @Size(min = 3, max = 100, message = "权限代码长度必须在 3-100 个字符之间")
  private String permissionCode;

  /** 权限名称，必填，1-100 个字符 */
  @NotBlank(message = "权限名称不能为空")
  @Size(max = 100, message = "权限名称长度不能超过 100 个字符")
  private String permissionName;

  /** 权限描述，可选 */
  private String description;
}
