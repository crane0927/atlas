/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.annotation;

/**
 * 逻辑关系枚举
 *
 * <p>用于定义权限或角色之间的逻辑关系，支持 AND（逻辑与）和 OR（逻辑或）两种关系。
 *
 * <p>使用场景：
 * <ul>
 *   <li>多个权限需要同时满足：使用 AND</li>
 *   <li>多个权限满足任一即可：使用 OR</li>
 *   <li>多个角色需要同时拥有：使用 AND</li>
 *   <li>多个角色拥有任一即可：使用 OR</li>
 * </ul>
 *
 * @author Atlas
 * @since 1.0.0
 */
public enum Logical {
  /**
   * 逻辑与，需要满足所有条件
   */
  AND,

  /**
   * 逻辑或，满足任一条件即可
   */
  OR
}

