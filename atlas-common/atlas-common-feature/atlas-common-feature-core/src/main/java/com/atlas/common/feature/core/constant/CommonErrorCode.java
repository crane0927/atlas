/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.constant;

/**
 * 通用错误码常量类
 *
 * <p>定义项目通用的错误码，模块码使用 05（atlas-common-feature）。 错误码格式：MMTTSS（6位数字） - MM: 模块码（05） - TT: 错误类型码（00-99）
 * - SS: 序号（00-99）
 *
 * <p>错误类型码分配： - 00-09: 系统错误 - 10-19: 参数错误 - 20-29: 业务错误 - 30-39: 权限错误 - 40-49: 数据错误 - 50-99: 预留扩展
 *
 * @author Atlas Team
 * @date 2026-01-05
 */
public class CommonErrorCode {

  // ========== 系统错误 (00-09) ==========

  /** 系统内部错误 */
  public static final String SYSTEM_ERROR = "050000";

  /** 服务暂时不可用 */
  public static final String SERVICE_UNAVAILABLE = "050001";

  /** 请求处理超时 */
  public static final String REQUEST_TIMEOUT = "050002";

  // ========== 参数错误 (10-19) ==========

  /** 参数错误 */
  public static final String PARAM_ERROR = "051000";

  /** 必填项缺失 */
  public static final String PARAM_REQUIRED = "051001";

  /** 参数格式错误 */
  public static final String PARAM_FORMAT_ERROR = "051002";

  // ========== 业务错误 (20-29) ==========

  /** 业务处理失败 */
  public static final String BUSINESS_ERROR = "052000";

  /** 数据不存在 */
  public static final String DATA_NOT_FOUND = "052001";

  /** 状态不正确 */
  public static final String STATUS_INVALID = "052002";

  // ========== 权限错误 (30-39) ==========

  /** 权限不足 */
  public static final String PERMISSION_DENIED = "053000";

  /** Token 无效 */
  public static final String TOKEN_INVALID = "053001";

  // ========== 数据错误 (40-49) ==========

  /** 数据错误 */
  public static final String DATA_ERROR = "054000";

  /** 数据冲突 */
  public static final String DATA_CONFLICT = "054001";

  /** 私有构造函数，防止实例化 */
  private CommonErrorCode() {
    throw new UnsupportedOperationException("常量类不允许实例化");
  }
}
