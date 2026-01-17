/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.role.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色创建 DTO
 *
 * <p>用于创建角色的请求数据传输对象。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>roleCode：角色代码，必填，3-50 个字符
 *   <li>roleName：角色名称，必填，1-100 个字符
 *   <li>description：角色描述，可选
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class RoleCreateDTO {

  /** 角色代码，必填，3-50 个字符 */
  @NotBlank(message = "角色代码不能为空")
  @Size(min = 3, max = 50, message = "角色代码长度必须在 3-50 个字符之间")
  private String roleCode;

  /** 角色名称，必填，1-100 个字符 */
  @NotBlank(message = "角色名称不能为空")
  @Size(max = 100, message = "角色名称长度不能超过 100 个字符")
  private String roleName;

  /** 角色描述，可选 */
  private String description;
}
