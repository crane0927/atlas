/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.model.dto;

import com.atlas.common.feature.core.page.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询条件 DTO
 *
 * <p>继承 {@link PageQueryDTO}，包含分页参数（page、size、sort）及用户业务查询条件。
 *
 * <p>业务字段说明：
 *
 * <ul>
 *   <li>username：用户名模糊匹配，可选
 *   <li>status：用户状态精确匹配，可选（如 ACTIVE、INACTIVE、DELETED）
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQueryDTO {

  /** 用户名模糊匹配，可选 */
  private String username;

  /** 用户状态，可选 */
  private String status;
}
