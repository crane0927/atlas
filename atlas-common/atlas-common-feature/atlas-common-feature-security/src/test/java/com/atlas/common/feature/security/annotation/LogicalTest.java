/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.annotation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Logical 枚举单元测试 */
class LogicalTest {

  @Test
  void testLogicalEnumValues() {
    // When & Then
    assertNotNull(Logical.AND);
    assertNotNull(Logical.OR);
    assertEquals(2, Logical.values().length);
  }

  @Test
  void testLogicalValueOf() {
    // When & Then
    assertEquals(Logical.AND, Logical.valueOf("AND"));
    assertEquals(Logical.OR, Logical.valueOf("OR"));
  }
}

