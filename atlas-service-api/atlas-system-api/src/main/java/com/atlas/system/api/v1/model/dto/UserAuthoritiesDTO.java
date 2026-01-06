/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.api.v1.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户权限信息 DTO
 *
 * <p>用户权限信息数据传输对象，包含用户角色和权限列表。
 *
 * <p>字段说明：
 * <ul>
 *   <li>userId：用户ID，必填字段，不能为 null</li>
 *   <li>roles：角色列表，必填字段，不能为 null（可以为空列表）</li>
 *   <li>permissions：权限列表，必填字段，不能为 null（可以为空列表）</li>
 * </ul>
 *
 * <p>向后兼容性：
 * <ul>
 *   <li>新增字段必须可空或提供默认值</li>
 *   <li>不允许删除或修改现有字段</li>
 *   <li>不允许修改字段类型或语义</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthoritiesDTO {

  /** 用户ID */
  private Long userId;

  /** 角色列表（不能为 null，可以为空列表） */
  private List<String> roles;

  /** 权限列表（不能为 null，可以为空列表） */
  private List<String> permissions;
}

