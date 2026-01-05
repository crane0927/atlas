/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.context;

import static org.junit.jupiter.api.Assertions.*;

import com.atlas.common.feature.security.user.LoginUser;
import java.util.Collections;
import org.junit.jupiter.api.Test;

/** SecurityContextHolder 单元测试 */
class SecurityContextHolderTest {

  /** 测试实现类：TestSecurityContextHolder */
  static class TestSecurityContextHolder extends SecurityContextHolder {
    private static SecurityContext context;

    public static void setContext(SecurityContext ctx) {
      context = ctx;
    }

    @Override
    public static SecurityContext getContext() {
      return context;
    }
  }

  /** 测试 SecurityContext 实现 */
  static class TestSecurityContext implements SecurityContext {
    private final LoginUser loginUser;

    public TestSecurityContext(LoginUser loginUser) {
      this.loginUser = loginUser;
    }

    @Override
    public LoginUser getLoginUser() {
      return loginUser;
    }

    @Override
    public boolean isAuthenticated() {
      return loginUser != null;
    }

    @Override
    public void clear() {
      // 测试实现，不做实际操作
    }
  }

  /** 测试 LoginUser 实现 */
  static class TestLoginUser implements LoginUser {
    private final Long userId;
    private final String username;

    public TestLoginUser(Long userId, String username) {
      this.userId = userId;
      this.username = username;
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
      return Collections.emptyList();
    }

    @Override
    public java.util.List<String> getPermissions() {
      return Collections.emptyList();
    }

    @Override
    public boolean hasRole(String role) {
      return false;
    }

    @Override
    public boolean hasPermission(String permission) {
      return false;
    }
  }

  @Test
  void testGetContext() {
    // Given
    SecurityContext context = new TestSecurityContext(null);
    TestSecurityContextHolder.setContext(context);

    // When
    SecurityContext result = TestSecurityContextHolder.getContext();

    // Then
    assertNotNull(result);
    assertEquals(context, result);
  }

  @Test
  void testGetLoginUser() {
    // Given
    LoginUser user = new TestLoginUser(1001L, "testuser");
    SecurityContext context = new TestSecurityContext(user);
    TestSecurityContextHolder.setContext(context);

    // When
    LoginUser result = TestSecurityContextHolder.getLoginUser();

    // Then
    assertNotNull(result);
    assertEquals(user, result);
    assertEquals(1001L, result.getUserId());
    assertEquals("testuser", result.getUsername());
  }

  @Test
  void testGetLoginUserWhenNotAuthenticated() {
    // Given
    SecurityContext context = new TestSecurityContext(null);
    TestSecurityContextHolder.setContext(context);

    // When
    LoginUser result = TestSecurityContextHolder.getLoginUser();

    // Then
    assertNull(result);
  }

  @Test
  void testGetLoginUserWhenContextIsNull() {
    // Given
    TestSecurityContextHolder.setContext(null);

    // When
    LoginUser result = TestSecurityContextHolder.getLoginUser();

    // Then
    assertNull(result);
  }

  @Test
  void testIsAuthenticated() {
    // Given
    LoginUser user = new TestLoginUser(1001L, "testuser");
    SecurityContext context = new TestSecurityContext(user);
    TestSecurityContextHolder.setContext(context);

    // When
    boolean authenticated = TestSecurityContextHolder.isAuthenticated();

    // Then
    assertTrue(authenticated);
  }

  @Test
  void testIsAuthenticatedWhenNotAuthenticated() {
    // Given
    SecurityContext context = new TestSecurityContext(null);
    TestSecurityContextHolder.setContext(context);

    // When
    boolean authenticated = TestSecurityContextHolder.isAuthenticated();

    // Then
    assertFalse(authenticated);
  }

  @Test
  void testIsAuthenticatedWhenContextIsNull() {
    // Given
    TestSecurityContextHolder.setContext(null);

    // When
    boolean authenticated = TestSecurityContextHolder.isAuthenticated();

    // Then
    assertFalse(authenticated);
  }
}

