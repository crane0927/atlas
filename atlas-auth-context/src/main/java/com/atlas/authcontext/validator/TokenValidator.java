/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.authcontext.validator;

import com.atlas.common.feature.security.user.LoginUser;

/**
 * Token 校验器（方案 B 抽象）
 *
 * <p>由 atlas-auth 等模块实现，atlas-auth-context 的 Filter 仅依赖此接口，不依赖具体 Token 实现（JWT + 黑名单等）。
 *
 * @author Atlas
 * @since 1.0.0
 */
public interface TokenValidator {

  /**
   * 校验 Token 并返回当前用户信息
   *
   * @param token 请求中的 Token 字符串
   * @return 校验通过时返回 LoginUser，无效或过期时返回 null
   */
  LoginUser validateToken(String token);
}
