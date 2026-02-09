/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应 VO
 *
 * <p>GET /api/v1/auth/captcha 返回：captchaKey 用于登录时提交，imageBase64 为 data URL 供前端 img 展示。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponseVO {

  /** 验证码唯一 key，登录时与 captchaCode 一并提交 */
  private String captchaKey;

  /** 图片 Base64（data URL：data:image/png;base64,...） */
  private String imageBase64;
}
