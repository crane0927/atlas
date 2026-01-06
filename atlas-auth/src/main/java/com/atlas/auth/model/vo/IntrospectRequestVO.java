/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.model.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token Introspection 请求 VO
 *
 * <p>Token Introspection 请求参数，用于 Gateway 验证 Token。
 *
 * <p>字段说明：
 * <ul>
 *   <li>token：待验证的 Token</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectRequestVO {

  /** 待验证的 Token */
  @NotBlank(message = "Token 不能为空")
  private String token;
}

