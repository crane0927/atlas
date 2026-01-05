/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.page;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

/**
 * 统一分页响应对象
 *
 * <p>用于封装分页查询结果，支持泛型，可以封装任意类型的数据列表。 自动计算总页数，提供便捷的分页信息查询方法。
 *
 * <p>使用示例：
 *
 * <pre>
 * PageResult&lt;User&gt; pageResult = PageResult.of(userList, totalCount, page, size);
 * </pre>
 *
 * @param <T> 数据列表元素类型
 * @author Atlas Team
 * @date 2026-01-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

  /** 数据列表 */
  private List<T> list;

  /** 总记录数 */
  private Long total;

  /** 当前页码（从 1 开始） */
  private Integer page;

  /** 每页大小 */
  private Integer size;

  /** 总页数（自动计算） */
  private Integer pages;

  /**
   * 链路追踪 ID
   *
   * <p>用于分布式追踪，关联一次请求在整个微服务系统中的完整调用链 如果未设置，将从 MDC 中自动获取
   */
  private String traceId;

  /**
   * 创建分页对象
   *
   * @param list 数据列表
   * @param total 总记录数
   * @param page 当前页码（从 1 开始）
   * @param size 每页大小
   * @param <T> 数据列表元素类型
   * @return PageResult 对象
   */
  public static <T> PageResult<T> of(List<T> list, Long total, Integer page, Integer size) {
    if (list == null) {
      list = Collections.emptyList();
    }
    if (total == null || total < 0) {
      total = 0L;
    }
    if (page == null || page < 1) {
      page = 1;
    }
    if (size == null || size < 1) {
      size = list.size() > 0 ? list.size() : 10;
    }

    // 计算总页数：向上取整
    int pages = total == 0 ? 0 : (int) ((total + size - 1) / size);

    return PageResult.<T>builder()
        .list(list)
        .total(total)
        .page(page)
        .size(size)
        .pages(pages)
        .traceId(getTraceId())
        .build();
  }

  /**
   * 创建分页对象（使用默认 page=1, size=list.size()）
   *
   * @param list 数据列表
   * @param total 总记录数
   * @param <T> 数据列表元素类型
   * @return PageResult 对象
   */
  public static <T> PageResult<T> of(List<T> list, Long total) {
    int size = list != null && !list.isEmpty() ? list.size() : 10;
    return of(list, total, 1, size);
  }

  /**
   * 是否有上一页
   *
   * @return true 表示有上一页，false 表示没有
   */
  public boolean hasPrevious() {
    return page > 1;
  }

  /**
   * 是否有下一页
   *
   * @return true 表示有下一页，false 表示没有
   */
  public boolean hasNext() {
    return page < pages;
  }

  /**
   * 是否是第一页
   *
   * @return true 表示是第一页，false 表示不是
   */
  public boolean isFirst() {
    return page == 1;
  }

  /**
   * 是否是最后一页
   *
   * @return true 表示是最后一页，false 表示不是
   */
  public boolean isLast() {
    return pages == 0 || page == pages;
  }

  /**
   * 从 MDC 获取 TraceId
   *
   * @return TraceId，如果不存在则返回 null
   */
  private static String getTraceId() {
    return MDC.get("traceId");
  }
}
