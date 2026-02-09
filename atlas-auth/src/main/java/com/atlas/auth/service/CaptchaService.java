/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.service;

import com.atlas.auth.model.vo.CaptchaResponseVO;

/**
 * 验证码服务
 *
 * <p>生成图形验证码、存储到 Redis、校验并一次性消费。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface CaptchaService {

  /**
   * 生成验证码：随机码 + 图片，并将答案存入 Redis。
   *
   * @return captchaKey 与 imageBase64（data URL）
   */
  CaptchaResponseVO generate();

  /**
   * 校验验证码并消费（校验成功后删除 Redis 中的 key）。不区分大小写。
   *
   * @param captchaKey 生成时返回的 key
   * @param userInput 用户输入的验证码
   * @return true 校验通过并已消费，false 校验失败或已过期/缺失
   */
  boolean validateAndConsume(String captchaKey, String userInput);
}
