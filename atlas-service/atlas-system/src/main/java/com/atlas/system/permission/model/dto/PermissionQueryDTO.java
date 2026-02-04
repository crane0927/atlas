/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.permission.model.dto;

import com.atlas.common.feature.core.page.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限分页查询条件 DTO
 *
 * <p>继承 {@link PageQueryDTO}，包含分页参数（page、size、sort）及权限业务查询条件。
 *
 * <p>业务字段说明：
 *
 * <ul>
 *   <li>permissionCode：权限代码模糊匹配，可选
 *   <li>permissionName：权限名称模糊匹配，可选
 *   <li>status：权限状态精确匹配，可选（如 ACTIVE、INACTIVE、DELETED）
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionQueryDTO extends PageQueryDTO {

  /** 权限代码模糊匹配，可选 */
  private String permissionCode;

  /** 权限名称模糊匹配，可选 */
  private String permissionName;

  /** 权限状态，可选 */
  private String status;
}
