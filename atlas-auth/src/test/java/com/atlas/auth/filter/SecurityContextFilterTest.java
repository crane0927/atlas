/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.filter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.auth.context.SecurityContextImpl;
import com.atlas.auth.model.dto.LoginUserImpl;
import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SecurityContextFilter 单元测试
 *
 * <p>测试安全上下文过滤器的功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class SecurityContextFilterTest {

  @Mock private TokenService tokenService;

  @Mock private SecurityContextImpl securityContext;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @InjectMocks private SecurityContextFilter filter;

  @BeforeEach
  void setUp() {
    filter = new SecurityContextFilter(tokenService, securityContext);
  }

  @Test
  void testDoFilterWithValidToken() throws Exception {
    // 准备测试数据
    String token = "valid-token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

    TokenInfoDTO tokenInfo = new TokenInfoDTO();
    tokenInfo.setTokenId("token-id-123");
    tokenInfo.setUserId(1L);
    tokenInfo.setUsername("admin");
    tokenInfo.setRoles(Arrays.asList("admin", "user"));
    tokenInfo.setPermissions(Arrays.asList("user:read", "user:write"));

    // Mock 服务调用
    when(tokenService.validateToken(token)).thenReturn(tokenInfo);

    // 执行过滤
    filter.doFilterInternal(request, response, filterChain);

    // 验证
    verify(tokenService).validateToken(token);
    verify(securityContext).setLoginUser(any(LoginUserImpl.class));
    verify(filterChain).doFilter(request, response);
    verify(securityContext).clear();
  }

  @Test
  void testDoFilterWithInvalidToken() throws Exception {
    // 准备测试数据
    String token = "invalid-token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

    // Mock 服务调用（Token 无效）
    when(tokenService.validateToken(token)).thenReturn(null);

    // 执行过滤
    filter.doFilterInternal(request, response, filterChain);

    // 验证
    verify(tokenService).validateToken(token);
    verify(securityContext, never()).setLoginUser(any(LoginUserImpl.class));
    verify(filterChain).doFilter(request, response);
    verify(securityContext).clear();
  }

  @Test
  void testDoFilterWithoutToken() throws Exception {
    // 准备测试数据（没有 Authorization 头）
    when(request.getHeader("Authorization")).thenReturn(null);

    // 执行过滤
    filter.doFilterInternal(request, response, filterChain);

    // 验证
    verify(tokenService, never()).validateToken(any(String.class));
    verify(securityContext, never()).setLoginUser(any(LoginUserImpl.class));
    verify(filterChain).doFilter(request, response);
    verify(securityContext).clear();
  }
}

