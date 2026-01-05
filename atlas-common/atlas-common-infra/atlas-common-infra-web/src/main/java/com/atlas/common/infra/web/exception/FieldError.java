/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段错误信息
 *
 * <p>用于封装单个字段的校验错误信息，包含字段名和错误消息。
 *
 * <p>使用示例：
 *
 * <pre>
 * FieldError error = FieldError.builder()
 *     .field("username")
 *     .message("用户名不能为空")
 *     .build();
 * </pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldError {

  /**
   * 字段名
   *
   * <p>发生校验错误的字段名称，不能为 null 或空字符串
   */
  private String field;

  /**
   * 错误消息
   *
   * <p>字段校验失败的错误消息，不能为 null 或空字符串
   */
  private String message;
}

