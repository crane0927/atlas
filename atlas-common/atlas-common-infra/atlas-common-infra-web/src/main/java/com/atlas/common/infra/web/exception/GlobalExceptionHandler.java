/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.exception;

import com.atlas.common.feature.core.constant.CommonErrorCode;
import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.exception.DataException;
import com.atlas.common.feature.core.exception.ParameterException;
import com.atlas.common.feature.core.exception.PermissionException;
import com.atlas.common.feature.core.result.Result;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * <p>统一处理所有 Controller 层抛出的异常，返回标准的 {@link Result} 格式响应。
 *
 * <p>支持的异常类型：
 *
 * <ul>
 *   <li>业务异常：{@link BusinessException}、{@link ParameterException}、{@link
 *       PermissionException}、{@link DataException}
 *   <li>参数校验异常：{@link MethodArgumentNotValidException}、{@link ConstraintViolationException}
 *   <li>Spring MVC 异常：{@link HttpRequestMethodNotSupportedException}、{@link
 *       HttpMediaTypeNotSupportedException}、{@link MissingServletRequestParameterException}
 *   <li>系统异常：{@link Exception}、{@link RuntimeException}
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>
 * // 在 Service 层抛出异常
 * throw new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "用户不存在");
 *
 * // 全局异常处理器会自动捕获并返回 Result 格式响应
 * </pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // ========== 业务异常处理 ==========

  /**
   * 处理业务异常
   *
   * @param e 业务异常
   * @return Result 错误响应
   */
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<Void> handleBusinessException(BusinessException e) {
    log.info("业务异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
    return Result.error(e.getErrorCode(), e.getMessage());
  }

  /**
   * 处理参数异常
   *
   * @param e 参数异常
   * @return Result 错误响应
   */
  @ExceptionHandler(ParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<Void> handleParameterException(ParameterException e) {
    log.info("参数异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
    return Result.error(e.getErrorCode(), e.getMessage());
  }

  /**
   * 处理权限异常
   *
   * @param e 权限异常
   * @return Result 错误响应
   */
  @ExceptionHandler(PermissionException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Result<Void> handlePermissionException(PermissionException e) {
    log.info("权限异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
    return Result.error(e.getErrorCode(), e.getMessage());
  }

  /**
   * 处理数据异常
   *
   * @param e 数据异常
   * @return Result 错误响应
   */
  @ExceptionHandler(DataException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Result<Void> handleDataException(DataException e) {
    log.info("数据异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
    return Result.error(e.getErrorCode(), e.getMessage());
  }

  // ========== 参数校验异常处理 ==========

  /**
   * 处理方法参数校验异常（@Valid 注解触发）
   *
   * @param e 方法参数校验异常
   * @return Result 错误响应，包含字段错误列表
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<ValidationError> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    List<org.springframework.validation.FieldError> fieldErrors =
        e.getBindingResult().getFieldErrors();
    ValidationError validationError = ValidationError.from(fieldErrors);
    log.info(
        "参数校验异常: fieldErrors={}",
        fieldErrors.stream().map(org.springframework.validation.FieldError::getField).toList());
    return Result.error(CommonErrorCode.PARAM_ERROR, "参数校验失败", validationError);
  }

  /**
   * 处理约束校验异常（@Validated 注解触发）
   *
   * @param e 约束校验异常
   * @return Result 错误响应，包含字段错误列表
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<ValidationError> handleConstraintViolationException(
      ConstraintViolationException e) {
    ValidationError validationError =
        ValidationError.fromConstraintViolations(e.getConstraintViolations().stream().toList());
    log.info("约束校验异常: violations={}", e.getConstraintViolations().size());
    return Result.error(CommonErrorCode.PARAM_ERROR, "参数校验失败", validationError);
  }

  /**
   * 处理绑定异常（表单数据绑定失败）
   *
   * @param e 绑定异常
   * @return Result 错误响应，包含字段错误列表
   */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<ValidationError> handleBindException(BindException e) {
    List<org.springframework.validation.FieldError> fieldErrors =
        e.getBindingResult().getFieldErrors();
    ValidationError validationError = ValidationError.from(fieldErrors);
    log.info(
        "绑定异常: fieldErrors={}",
        fieldErrors.stream().map(org.springframework.validation.FieldError::getField).toList());
    return Result.error(CommonErrorCode.PARAM_ERROR, "参数绑定失败", validationError);
  }

  // ========== Spring MVC 异常处理 ==========

  /**
   * 处理 HTTP 方法不支持异常
   *
   * @param e HTTP 方法不支持异常
   * @return Result 错误响应
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public Result<Void> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    String message =
        String.format("HTTP 方法 '%s' 不支持，支持的方法: %s", e.getMethod(), e.getSupportedMethods());
    log.warn("HTTP 方法不支持: method={}, supportedMethods={}", e.getMethod(), e.getSupportedMethods());
    return Result.error(CommonErrorCode.PARAM_ERROR, message);
  }

  /**
   * 处理 HTTP 媒体类型不支持异常
   *
   * @param e HTTP 媒体类型不支持异常
   * @return Result 错误响应
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  public Result<Void> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException e) {
    String message =
        String.format(
            "HTTP 媒体类型 '%s' 不支持，支持的媒体类型: %s", e.getContentType(), e.getSupportedMediaTypes());
    log.warn(
        "HTTP 媒体类型不支持: contentType={}, supportedMediaTypes={}",
        e.getContentType(),
        e.getSupportedMediaTypes());
    return Result.error(CommonErrorCode.PARAM_ERROR, message);
  }

  /**
   * 处理缺少请求参数异常
   *
   * @param e 缺少请求参数异常
   * @return Result 错误响应
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<Void> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException e) {
    String message =
        String.format("缺少必需的请求参数: %s (类型: %s)", e.getParameterName(), e.getParameterType());
    log.warn(
        "缺少请求参数: parameterName={}, parameterType={}", e.getParameterName(), e.getParameterType());
    return Result.error(CommonErrorCode.PARAM_REQUIRED, message);
  }

  // ========== 系统异常处理 ==========

  /**
   * 处理系统异常（兜底处理）
   *
   * <p>处理所有未被上述方法捕获的异常，返回通用错误响应。 生产环境不暴露异常堆栈信息，避免泄露敏感信息。
   *
   * @param e 系统异常
   * @return Result 错误响应
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleException(Exception e) {
    log.error("系统异常", e);
    return Result.error(CommonErrorCode.SYSTEM_ERROR, "系统错误，请联系管理员");
  }
}
