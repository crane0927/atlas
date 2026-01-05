/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError as SpringFieldError;

/**
 * 参数校验错误信息封装类
 *
 * <p>用于封装参数校验失败的错误信息，包含字段错误列表。 用于全局异常处理器处理参数校验异常时返回统一的错误格式。
 *
 * <p>使用示例：
 *
 * <pre>
 * List&lt;FieldError&gt; errors = new ArrayList&lt;&gt;();
 * errors.add(FieldError.builder()
 *     .field("username")
 *     .message("用户名不能为空")
 *     .build());
 * ValidationError validationError = ValidationError.builder()
 *     .errors(errors)
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
public class ValidationError {

  /**
   * 字段错误列表
   *
   * <p>包含所有校验失败的字段错误信息，不能为 null，可以为空列表
   */
  @NotNull private List<FieldError> errors;

  /**
   * 从 Spring Validation 的 FieldError 列表创建 ValidationError
   *
   * <p>将 Spring Validation 的 FieldError 转换为本模块的 FieldError 格式
   *
   * @param springFieldErrors Spring Validation 的 FieldError 列表
   * @return ValidationError 对象
   */
  public static ValidationError from(List<SpringFieldError> springFieldErrors) {
    if (springFieldErrors == null || springFieldErrors.isEmpty()) {
      return ValidationError.builder().errors(new ArrayList<>()).build();
    }

    List<FieldError> errors = new ArrayList<>();
    for (SpringFieldError springFieldError : springFieldErrors) {
      errors.add(
          FieldError.builder()
              .field(springFieldError.getField())
              .message(springFieldError.getDefaultMessage())
              .build());
    }

    return ValidationError.builder().errors(errors).build();
  }

  /**
   * 从 Spring Validation 的 ConstraintViolation 创建 ValidationError
   *
   * <p>将 Spring Validation 的 ConstraintViolation 转换为本模块的 FieldError 格式
   *
   * @param violations ConstraintViolation 列表
   * @return ValidationError 对象
   */
  public static ValidationError fromConstraintViolations(
      List<jakarta.validation.ConstraintViolation<?>> violations) {
    if (violations == null || violations.isEmpty()) {
      return ValidationError.builder().errors(new ArrayList<>()).build();
    }

    List<FieldError> errors = new ArrayList<>();
    for (jakarta.validation.ConstraintViolation<?> violation : violations) {
      String fieldName =
          violation.getPropertyPath() != null
              ? violation.getPropertyPath().toString()
              : "unknown";
      errors.add(
          FieldError.builder().field(fieldName).message(violation.getMessage()).build());
    }

    return ValidationError.builder().errors(errors).build();
  }
}

