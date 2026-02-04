/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.role.model.dto;

import com.atlas.common.feature.core.page.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询条件 DTO
 *
 * <p>继承 {@link PageQueryDTO}，包含分页参数（page、size、sort）及角色业务查询条件。
 *
 * <p>业务字段说明：
 *
 * <ul>
 *   <li>roleCode：角色代码模糊匹配，可选
 *   <li>roleName：角色名称模糊匹配，可选
 *   <li>status：角色状态精确匹配，可选（如 ACTIVE、INACTIVE、DELETED）
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryDTO extends PageQueryDTO {

  /** 角色代码模糊匹配，可选 */
  private String roleCode;

  /** 角色名称模糊匹配，可选 */
  private String roleName;

  /** 角色状态，可选 */
  private String status;
}
