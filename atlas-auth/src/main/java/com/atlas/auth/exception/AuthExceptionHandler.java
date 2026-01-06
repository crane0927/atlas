/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.exception;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.exception.DataException;
import com.atlas.common.feature.core.exception.ParameterException;
import com.atlas.common.feature.core.exception.PermissionException;
import com.atlas.common.feature.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Auth 模块全局异常处理器
 *
 * <p>统一处理 Auth 模块的异常，返回统一的错误响应格式。
 *
 * <p>处理的异常类型：
 * <ul>
 *   <li>BusinessException - 业务异常</li>
 *   <li>ParameterException - 参数异常</li>
 *   <li>PermissionException - 权限异常</li>
 *   <li>DataException - 数据异常</li>
 *   <li>MethodArgumentNotValidException - 参数校验异常</li>
 *   <li>BindException - 绑定异常</li>
 *   <li>其他异常 - 系统异常</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.atlas.auth")
public class AuthExceptionHandler {

  /**
   * 处理业务异常
   *
   * @param e 业务异常
   * @return 错误响应
   */
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<Void> handleBusinessException(BusinessException e) {
    log.warn("业务异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
    return Result.error(e.getErrorCode(), e.getMessage());
  }

  /**
   * 处理参数异常
   *
   * @param e 参数异常
   * @return 错误响应
   */
  @ExceptionHandler(ParameterException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<Void> handleParameterException(ParameterException e) {
    log.warn("参数异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
    return Result.error(e.getErrorCode(), e.getMessage());
  }

  /**
   * 处理权限异常
   *
   * @param e 权限异常
   * @return 错误响应
   */
  @ExceptionHandler(PermissionException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<Void> handlePermissionException(PermissionException e) {
    log.warn("权限异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
    return Result.error(e.getErrorCode(), e.getMessage());
  }

  /**
   * 处理数据异常
   *
   * @param e 数据异常
   * @return 错误响应
   */
  @ExceptionHandler(DataException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<Void> handleDataException(DataException e) {
    log.warn("数据异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
    return Result.error(e.getErrorCode(), e.getMessage());
  }

  /**
   * 处理参数校验异常（@Valid 注解）
   *
   * @param e 参数校验异常
   * @return 错误响应
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    FieldError fieldError = e.getBindingResult().getFieldError();
    String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
    log.warn("参数校验异常: message={}", message);
    return Result.error(com.atlas.common.feature.core.constant.CommonErrorCode.PARAM_REQUIRED, message);
  }

  /**
   * 处理绑定异常
   *
   * @param e 绑定异常
   * @return 错误响应
   */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<Void> handleBindException(BindException e) {
    FieldError fieldError = e.getBindingResult().getFieldError();
    String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
    log.warn("参数绑定异常: message={}", message);
    return Result.error(com.atlas.common.feature.core.constant.CommonErrorCode.PARAM_REQUIRED, message);
  }

  /**
   * 处理其他异常（系统异常）
   *
   * @param e 异常
   * @return 错误响应
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.OK)
  public Result<Void> handleException(Exception e) {
    log.error("系统异常", e);
    return Result.error(
        com.atlas.common.feature.core.constant.CommonErrorCode.SYSTEM_ERROR, "系统错误，请稍后重试");
  }
}

