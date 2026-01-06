/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.api.v1.model.enums;

/**
 * 用户状态枚举
 *
 * <p>定义用户的各种状态，用于标识用户的当前状态。
 *
 * <p>枚举值说明：
 * <ul>
 *   <li>ACTIVE：激活状态，用户正常使用</li>
 *   <li>INACTIVE：未激活状态，用户尚未激活</li>
 *   <li>LOCKED：锁定状态，用户被锁定，无法使用</li>
 *   <li>DELETED：已删除状态，用户已被删除</li>
 * </ul>
 *
 * <p>序列化说明：
 * <ul>
 *   <li>枚举值序列化为字符串（枚举名称），如 "ACTIVE"</li>
 *   <li>支持 JSON 序列化/反序列化</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public enum UserStatus {

  /** 激活状态 - 用户正常使用 */
  ACTIVE,

  /** 未激活状态 - 用户尚未激活 */
  INACTIVE,

  /** 锁定状态 - 用户被锁定，无法使用 */
  LOCKED,

  /** 已删除状态 - 用户已被删除 */
  DELETED
}

