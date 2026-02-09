/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.service.impl;

import com.atlas.auth.config.AuthProperties;
import com.atlas.auth.model.vo.CaptchaResponseVO;
import com.atlas.auth.service.CaptchaService;
import com.atlas.common.infra.redis.util.CacheUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 图形验证码服务实现
 *
 * <p>使用 AWT 生成随机字母数字码与图片，答案存 Redis，TTL 由配置决定。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

  private static final String REDIS_KEY_PREFIX = "captcha:";
  private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
  private static final int IMAGE_WIDTH = 120;
  private static final int IMAGE_HEIGHT = 40;
  private static final String IMAGE_FORMAT = "png";

  private final AuthProperties authProperties;

  public CaptchaServiceImpl(AuthProperties authProperties) {
    this.authProperties = authProperties;
  }

  @Override
  public CaptchaResponseVO generate() {
    int length = Math.max(4, Math.min(6, authProperties.getCaptcha().getLength()));
    int ttlSeconds = authProperties.getCaptcha().getTtlSeconds();
    String code = randomCode(length);
    String captchaKey = UUID.randomUUID().toString();
    String redisKey = REDIS_KEY_PREFIX + captchaKey;
    CacheUtil.set(redisKey, code, ttlSeconds);

    byte[] imageBytes = drawImage(code);
    String base64 = Base64.getEncoder().encodeToString(imageBytes);
    String imageBase64 = "data:image/" + IMAGE_FORMAT + ";base64," + base64;

    return new CaptchaResponseVO(captchaKey, imageBase64);
  }

  @Override
  public boolean validateAndConsume(String captchaKey, String userInput) {
    if (captchaKey == null || captchaKey.isBlank()) {
      return false;
    }
    String redisKey = REDIS_KEY_PREFIX + captchaKey.trim();
    String expected = CacheUtil.get(redisKey, String.class);
    if (expected == null) {
      log.debug("验证码不存在或已过期: captchaKey={}", captchaKey);
      return false;
    }
    boolean match = expected.equalsIgnoreCase(userInput != null ? userInput.trim() : "");
    CacheUtil.delete(redisKey);
    return match;
  }

  private static String randomCode(int length) {
    StringBuilder sb = new StringBuilder(length);
    java.security.SecureRandom r = new java.security.SecureRandom();
    for (int i = 0; i < length; i++) {
      sb.append(CHARS.charAt(r.nextInt(CHARS.length())));
    }
    return sb.toString();
  }

  private static byte[] drawImage(String code) {
    BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    g.setColor(Color.DARK_GRAY);
    g.setFont(new Font("SansSerif", Font.BOLD, 28));
    int x = 12;
    for (int i = 0; i < code.length(); i++) {
      g.drawString(String.valueOf(code.charAt(i)), x, 28);
      x += 26;
    }
    g.dispose();

    try {
      java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
      ImageIO.write(image, IMAGE_FORMAT, baos);
      return baos.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("生成验证码图片失败", e);
    }
  }
}
