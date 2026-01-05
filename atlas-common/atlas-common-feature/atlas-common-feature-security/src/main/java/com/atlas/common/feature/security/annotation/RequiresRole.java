/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色检查注解
 *
 * <p>用于在方法或类上声明角色要求。权限检查框架（后续实现）会根据此注解进行角色验证。
 *
 * <p>使用规则：
 * <ul>
 *   <li>可以应用于类级别和方法级别</li>
 *   <li>方法级别的注解会覆盖类级别的注解</li>
 *   <li>value 数组为空时表示不需要角色（通常不推荐）</li>
 *   <li>logical = AND 表示需要拥有所有角色</li>
 *   <li>logical = OR 表示需要拥有任一角色</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * // 需要 ADMIN 角色
 * &#64;RequiresRole("ADMIN")
 * public class AdminController {
 *     // 需要 ADMIN 或 MANAGER 角色
 *     &#64;RequiresRole(value = {"ADMIN", "MANAGER"}, logical = Logical.OR)
 *     public void manageUsers() {
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
public @interface RequiresRole {

  /**
   * 所需的角色列表
   *
   * @return 角色名称数组
   */
  String[] value();

  /**
   * 角色之间的逻辑关系
   *
   * <p>默认为 AND，表示需要拥有所有角色。
   * 设置为 OR 时，表示拥有任一角色即可。
   *
   * @return 逻辑关系，默认为 AND
   */
  Logical logical() default Logical.AND;
}

