/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.constant;

/**
 * 通用常量类
 *
 * <p>定义项目通用的常量值，包括字符串常量、数字常量等。
 *
 * @author Atlas Team
 * @date 2026-01-05
 */
public class CommonConstants {

  /** 空字符串 */
  public static final String EMPTY_STRING = "";

  /** 默认页码 */
  public static final Integer DEFAULT_PAGE = 1;

  /** 默认每页大小 */
  public static final Integer DEFAULT_SIZE = 10;

  /** 最大每页大小 */
  public static final Integer MAX_PAGE_SIZE = 1000;

  /** 成功状态码 */
  public static final String SUCCESS_CODE = "000000";

  /** 成功消息 */
  public static final String SUCCESS_MESSAGE = "操作成功";

  /** 私有构造函数，防止实例化 */
  private CommonConstants() {
    throw new UnsupportedOperationException("常量类不允许实例化");
  }
}
