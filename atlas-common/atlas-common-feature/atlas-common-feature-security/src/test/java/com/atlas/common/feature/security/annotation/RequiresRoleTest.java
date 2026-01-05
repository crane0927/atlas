/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.annotation;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;

/** @RequiresRole 注解单元测试 */
class RequiresRoleTest {

  @RequiresRole("ADMIN")
  static class TestClass {}

  @RequiresRole(value = {"ADMIN", "MANAGER"}, logical = Logical.OR)
  static class TestClassWithMultipleRoles {}

  @RequiresRole(value = "USER", logical = Logical.AND)
  void testMethod() {}

  @Test
  void testRequiresRoleAnnotationAttributes() {
    // Given
    RequiresRole annotation = TestClass.class.getAnnotation(RequiresRole.class);

    // When & Then
    assertNotNull(annotation);
    assertEquals(1, annotation.value().length);
    assertEquals("ADMIN", annotation.value()[0]);
    assertEquals(Logical.AND, annotation.logical()); // 默认值
  }

  @Test
  void testRequiresRoleWithMultipleRoles() {
    // Given
    RequiresRole annotation =
        TestClassWithMultipleRoles.class.getAnnotation(RequiresRole.class);

    // When & Then
    assertNotNull(annotation);
    assertEquals(2, annotation.value().length);
    assertEquals("ADMIN", annotation.value()[0]);
    assertEquals("MANAGER", annotation.value()[1]);
    assertEquals(Logical.OR, annotation.logical());
  }

  @Test
  void testRequiresRoleWithLogicalAND() throws NoSuchMethodException {
    // Given
    RequiresRole annotation =
        RequiresRoleTest.class.getDeclaredMethod("testMethod").getAnnotation(RequiresRole.class);

    // When & Then
    assertNotNull(annotation);
    assertEquals(1, annotation.value().length);
    assertEquals("USER", annotation.value()[0]);
    assertEquals(Logical.AND, annotation.logical());
  }

  @Test
  void testRequiresRoleTarget() {
    // Given
    Target target = RequiresRole.class.getAnnotation(Target.class);

    // When & Then
    assertNotNull(target);
    ElementType[] types = target.value();
    assertEquals(2, types.length);
    assertTrue(
        java.util.Arrays.asList(types).contains(ElementType.TYPE)
            && java.util.Arrays.asList(types).contains(ElementType.METHOD));
  }

  @Test
  void testRequiresRoleRetention() {
    // Given
    Retention retention = RequiresRole.class.getAnnotation(Retention.class);

    // When & Then
    assertNotNull(retention);
    assertEquals(RetentionPolicy.RUNTIME, retention.value());
  }

  @Test
  void testRequiresRoleOnClass() {
    // Given
    Annotation annotation = TestClass.class.getAnnotation(RequiresRole.class);

    // When & Then
    assertNotNull(annotation);
    assertTrue(annotation instanceof RequiresRole);
  }

  @Test
  void testRequiresRoleOnMethod() throws NoSuchMethodException {
    // Given
    Annotation annotation =
        RequiresRoleTest.class.getDeclaredMethod("testMethod").getAnnotation(RequiresRole.class);

    // When & Then
    assertNotNull(annotation);
    assertTrue(annotation instanceof RequiresRole);
  }
}

