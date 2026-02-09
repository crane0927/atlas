/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.config;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.infra.web.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

/**
 * Web 异常处理自动配置。
 *
 * <p>为 Web 环境自动注册统一异常处理器，避免业务服务显式导入配置。
 *
 * <p>当依赖缺失或非 Servlet 环境时，自动配置不会生效，服务回退到默认异常处理链路。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(BusinessException.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import(GlobalExceptionHandler.class)
public class WebExceptionAutoConfiguration {}
