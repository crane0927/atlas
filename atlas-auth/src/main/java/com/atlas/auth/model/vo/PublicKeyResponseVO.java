/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT 公钥响应 VO
 *
 * <p>JWT 公钥响应数据，用于 Gateway 获取公钥进行 Token 验证。
 *
 * <p>字段说明：
 * <ul>
 *   <li>algorithm：算法（如 "RS256"）</li>
 *   <li>publicKey：公钥（PEM 格式）</li>
 *   <li>keyId：密钥ID（用于公钥轮换）</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeyResponseVO {

  /** 算法（如 "RS256"） */
  private String algorithm;

  /** 公钥（PEM 格式） */
  private String publicKey;

  /** 密钥ID（用于公钥轮换） */
  private String keyId;
}

