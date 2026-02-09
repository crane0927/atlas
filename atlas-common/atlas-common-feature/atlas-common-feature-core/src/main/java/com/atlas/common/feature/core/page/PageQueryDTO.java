/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用分页查询请求 DTO
 *
 * <p>用于统一所有分页接口的请求参数语义，与 {@link PageResult} 响应对象对应。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>page：当前页码，从 1 开始，默认 1
 *   <li>size：每页条数，默认 10，建议上限 100
 *   <li>sort：排序，格式为「字段名,方向」，如 createTime,desc 或 username,asc
 * </ul>
 *
 * <p>使用方式：Controller 层通过 QueryParam 绑定，或与业务 QueryDTO 组合使用；排序字段由各业务层白名单校验。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryDTO {

  /** 当前页码，从 1 开始，默认 1 */
  @Builder.Default private Integer page = 1;

  /** 每页条数，默认 10，建议不超过 100 */
  @Builder.Default private Integer size = 10;

  /** 排序，格式：字段名,asc 或 字段名,desc，可选 */
  private String sort;

  /**
   * 获取有效的页码（至少为 1）
   *
   * @return 页码，若未设置或小于 1 则返回 1
   */
  public int getPageSafe() {
    if (page == null || page < 1) {
      return 1;
    }
    return page;
  }

  /**
   * 获取有效的每页条数（1～100）
   *
   * @return 每页条数，若未设置或小于 1 则返回 10，若大于 100 则返回 100
   */
  public int getSizeSafe() {
    if (size == null || size < 1) {
      return 10;
    }
    if (size > 100) {
      return 100;
    }
    return size;
  }
}
