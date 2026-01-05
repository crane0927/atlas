/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.constant;

/**
 * HTTP 状态码常量类
 *
 * <p>定义常用的 HTTP 状态码常量，便于代码中使用。
 *
 * @author Atlas Team
 * @date 2026-01-05
 */
public class HttpStatus {

  /** 请求成功 */
  public static final int OK = 200;

  /** 创建成功 */
  public static final int CREATED = 201;

  /** 无内容 */
  public static final int NO_CONTENT = 204;

  /** 请求错误 */
  public static final int BAD_REQUEST = 400;

  /** 未授权 */
  public static final int UNAUTHORIZED = 401;

  /** 禁止访问 */
  public static final int FORBIDDEN = 403;

  /** 资源不存在 */
  public static final int NOT_FOUND = 404;

  /** 服务器内部错误 */
  public static final int INTERNAL_SERVER_ERROR = 500;

  /** 私有构造函数，防止实例化 */
  private HttpStatus() {
    throw new UnsupportedOperationException("常量类不允许实例化");
  }
}
