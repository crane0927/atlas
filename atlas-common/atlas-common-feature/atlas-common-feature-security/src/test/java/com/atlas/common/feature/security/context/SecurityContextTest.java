/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.context;

import static org.junit.jupiter.api.Assertions.*;

import com.atlas.common.feature.security.user.LoginUser;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

/** SecurityContext 接口单元测试 */
class SecurityContextTest {

  /** 测试实现类：ThreadLocalSecurityContext */
  static class ThreadLocalSecurityContext implements SecurityContext {
    private static final ThreadLocal<LoginUser> context = new ThreadLocal<>();

    public void setLoginUser(LoginUser user) {
      if (user != null) {
        context.set(user);
      } else {
        context.remove();
      }
    }

    @Override
    public LoginUser getLoginUser() {
      return context.get();
    }

    @Override
    public boolean isAuthenticated() {
      return context.get() != null;
    }

    @Override
    public void clear() {
      context.remove();
    }
  }

  /** 测试 LoginUser 实现 */
  static class TestLoginUser implements LoginUser {
    private final Long userId;
    private final String username;
    private final java.util.List<String> roles;
    private final java.util.List<String> permissions;

    public TestLoginUser(
        Long userId, String username, java.util.List<String> roles, java.util.List<String> permissions) {
      this.userId = userId;
      this.username = username;
      this.roles = roles != null ? roles : Collections.emptyList();
      this.permissions = permissions != null ? permissions : Collections.emptyList();
    }

    @Override
    public Object getUserId() {
      return userId;
    }

    @Override
    public String getUsername() {
      return username;
    }

    @Override
    public java.util.List<String> getRoles() {
      return roles;
    }

    @Override
    public java.util.List<String> getPermissions() {
      return permissions;
    }

    @Override
    public boolean hasRole(String role) {
      return roles.contains(role);
    }

    @Override
    public boolean hasPermission(String permission) {
      return permissions.contains(permission);
    }
  }

  @Test
  void testGetLoginUser() {
    // Given
    ThreadLocalSecurityContext context = new ThreadLocalSecurityContext();
    LoginUser user = new TestLoginUser(1001L, "testuser", Arrays.asList("ADMIN"), null);

    // When
    context.setLoginUser(user);
    LoginUser result = context.getLoginUser();

    // Then
    assertNotNull(result);
    assertEquals(user, result);
    assertEquals(1001L, result.getUserId());
    assertEquals("testuser", result.getUsername());
  }

  @Test
  void testGetLoginUserWhenNotAuthenticated() {
    // Given
    ThreadLocalSecurityContext context = new ThreadLocalSecurityContext();

    // When
    LoginUser result = context.getLoginUser();

    // Then
    assertNull(result);
  }

  @Test
  void testIsAuthenticated() {
    // Given
    ThreadLocalSecurityContext context = new ThreadLocalSecurityContext();
    LoginUser user = new TestLoginUser(1001L, "testuser", null, null);

    // When
    context.setLoginUser(user);
    boolean authenticated = context.isAuthenticated();

    // Then
    assertTrue(authenticated);
  }

  @Test
  void testIsAuthenticatedWhenNotAuthenticated() {
    // Given
    ThreadLocalSecurityContext context = new ThreadLocalSecurityContext();

    // When
    boolean authenticated = context.isAuthenticated();

    // Then
    assertFalse(authenticated);
  }

  @Test
  void testIsAuthenticatedConsistentWithGetLoginUser() {
    // Given
    ThreadLocalSecurityContext context = new ThreadLocalSecurityContext();
    LoginUser user = new TestLoginUser(1001L, "testuser", null, null);

    // When
    context.setLoginUser(user);
    boolean authenticated = context.isAuthenticated();
    LoginUser loginUser = context.getLoginUser();

    // Then
    assertEquals(loginUser != null, authenticated);
  }

  @Test
  void testClear() {
    // Given
    ThreadLocalSecurityContext context = new ThreadLocalSecurityContext();
    LoginUser user = new TestLoginUser(1001L, "testuser", null, null);
    context.setLoginUser(user);

    // When
    context.clear();
    LoginUser result = context.getLoginUser();
    boolean authenticated = context.isAuthenticated();

    // Then
    assertNull(result);
    assertFalse(authenticated);
  }
}

