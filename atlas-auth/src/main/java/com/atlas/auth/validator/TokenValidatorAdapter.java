/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.auth.validator;

import com.atlas.auth.model.dto.LoginUserDTO;
import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.service.TokenService;
import com.atlas.common.feature.security.user.LoginUser;
import com.atlas.common.feature.security.validator.TokenValidator;
import java.util.Collections;
import org.springframework.stereotype.Component;

/**
 * TokenValidator 适配器
 *
 * <p>委托 {@link TokenService} 校验 Token，将 TokenInfoDTO 转为 LoginUser 供 SecurityContextFilter 使用。
 *
 * @author Atlas
 * @since 1.0.0
 */
@Component
public class TokenValidatorAdapter implements TokenValidator {

  private final TokenService tokenService;

  public TokenValidatorAdapter(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  public LoginUser validateToken(String token) {
    TokenInfoDTO dto = tokenService.validateToken(token);
    if (dto == null) {
      return null;
    }
    LoginUserDTO user = new LoginUserDTO();
    user.setUserId(dto.getUserId());
    user.setUsername(dto.getUsername());
    user.setRoles(dto.getRoles() != null ? dto.getRoles() : Collections.emptyList());
    user.setPermissions(
        dto.getPermissions() != null ? dto.getPermissions() : Collections.emptyList());
    return user;
  }
}
