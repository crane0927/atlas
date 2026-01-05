/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限检查注解
 *
 * <p>用于在方法或类上声明权限要求。权限检查框架（后续实现）会根据此注解进行权限验证。
 *
 * <p>使用规则：
 * <ul>
 *   <li>可以应用于类级别和方法级别</li>
 *   <li>方法级别的注解会覆盖类级别的注解</li>
 *   <li>value 数组为空时表示不需要权限（通常不推荐）</li>
 *   <li>logical = AND 表示需要拥有所有权限</li>
 *   <li>logical = OR 表示需要拥有任一权限</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * // 类级别：需要 user:read 权限
 * &#64;RequiresPermission("user:read")
 * public class UserController {
 *     // 方法级别：需要 user:write 权限（覆盖类级别）
 *     &#64;RequiresPermission(value = "user:write", logical = Logical.OR)
 *     public void updateUser() {
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @author Atlas
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

  /**
   * 所需的权限列表
   *
   * @return 权限名称数组
   */
  String[] value();

  /**
   * 权限之间的逻辑关系
   *
   * <p>默认为 AND，表示需要拥有所有权限。
   * 设置为 OR 时，表示拥有任一权限即可。
   *
   * @return 逻辑关系，默认为 AND
   */
  Logical logical() default Logical.AND;
}

