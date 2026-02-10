/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.springframework.util.StringUtils;

/**
 * 排序工具类
 *
 * <p>为分页/列表查询提供统一的 sort 参数解析与白名单应用，避免各 Service 重复实现。 sort 格式：字段名,asc 或 字段名,desc；未指定方向时默认 asc。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public final class SortHelper {

  private SortHelper() {}

  /**
   * 根据 sort 字符串对 wrapper 应用排序
   *
   * @param wrapper 查询包装器
   * @param sort 排序字符串，如 "createdAt,desc" 或 "username"
   * @param defaultOrder 当 sort 为空或不在白名单时应用的默认排序
   * @param fieldOrderAppliers 白名单：字段名（小写） -> (wrapper, asc) 应用该字段排序
   * @param <T> 实体类型
   */
  public static <T> void applySort(
      LambdaQueryWrapper<T> wrapper,
      String sort,
      Consumer<LambdaQueryWrapper<T>> defaultOrder,
      Map<String, BiConsumer<LambdaQueryWrapper<T>, Boolean>> fieldOrderAppliers) {
    if (!StringUtils.hasText(sort)) {
      defaultOrder.accept(wrapper);
      return;
    }
    String[] parts = sort.split(",");
    String field = parts.length > 0 ? parts[0].trim() : "";
    boolean asc = parts.length <= 1 || !"desc".equalsIgnoreCase(parts[1].trim());
    BiConsumer<LambdaQueryWrapper<T>, Boolean> applier =
        fieldOrderAppliers.get(field.toLowerCase());
    if (applier != null) {
      applier.accept(wrapper, asc);
    } else {
      defaultOrder.accept(wrapper);
    }
  }
}
