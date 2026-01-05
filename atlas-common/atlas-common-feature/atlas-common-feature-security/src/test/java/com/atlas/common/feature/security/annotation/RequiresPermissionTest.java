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

/** @RequiresPermission 注解单元测试 */
class RequiresPermissionTest {

  @RequiresPermission("user:read")
  static class TestClass {}

  @RequiresPermission(value = {"user:read", "user:write"}, logical = Logical.AND)
  static class TestClassWithMultiplePermissions {}

  @RequiresPermission(value = "user:write", logical = Logical.OR)
  void testMethod() {}

  @Test
  void testRequiresPermissionAnnotationAttributes() {
    // Given
    RequiresPermission annotation = TestClass.class.getAnnotation(RequiresPermission.class);

    // When & Then
    assertNotNull(annotation);
    assertEquals(1, annotation.value().length);
    assertEquals("user:read", annotation.value()[0]);
    assertEquals(Logical.AND, annotation.logical()); // 默认值
  }

  @Test
  void testRequiresPermissionWithMultiplePermissions() {
    // Given
    RequiresPermission annotation =
        TestClassWithMultiplePermissions.class.getAnnotation(RequiresPermission.class);

    // When & Then
    assertNotNull(annotation);
    assertEquals(2, annotation.value().length);
    assertEquals("user:read", annotation.value()[0]);
    assertEquals("user:write", annotation.value()[1]);
    assertEquals(Logical.AND, annotation.logical());
  }

  @Test
  void testRequiresPermissionWithLogicalOR() throws NoSuchMethodException {
    // Given
    RequiresPermission annotation =
        RequiresPermissionTest.class
            .getDeclaredMethod("testMethod")
            .getAnnotation(RequiresPermission.class);

    // When & Then
    assertNotNull(annotation);
    assertEquals(1, annotation.value().length);
    assertEquals("user:write", annotation.value()[0]);
    assertEquals(Logical.OR, annotation.logical());
  }

  @Test
  void testRequiresPermissionTarget() {
    // Given
    Target target = RequiresPermission.class.getAnnotation(Target.class);

    // When & Then
    assertNotNull(target);
    ElementType[] types = target.value();
    assertEquals(2, types.length);
    assertTrue(
        java.util.Arrays.asList(types).contains(ElementType.TYPE)
            && java.util.Arrays.asList(types).contains(ElementType.METHOD));
  }

  @Test
  void testRequiresPermissionRetention() {
    // Given
    Retention retention = RequiresPermission.class.getAnnotation(Retention.class);

    // When & Then
    assertNotNull(retention);
    assertEquals(RetentionPolicy.RUNTIME, retention.value());
  }

  @Test
  void testRequiresPermissionOnClass() {
    // Given
    Annotation annotation = TestClass.class.getAnnotation(RequiresPermission.class);

    // When & Then
    assertNotNull(annotation);
    assertTrue(annotation instanceof RequiresPermission);
  }

  @Test
  void testRequiresPermissionOnMethod() throws NoSuchMethodException {
    // Given
    Annotation annotation =
        RequiresPermissionTest.class
            .getDeclaredMethod("testMethod")
            .getAnnotation(RequiresPermission.class);

    // When & Then
    assertNotNull(annotation);
    assertTrue(annotation instanceof RequiresPermission);
  }
}

