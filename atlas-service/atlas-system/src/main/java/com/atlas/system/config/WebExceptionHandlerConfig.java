/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Web 异常处理器配置
 *
 * <p>配置全局异常处理器的扫描路径，确保 GlobalExceptionHandler 被正确加载。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(name = "com.atlas.common.feature.core.exception.BusinessException")
@ComponentScan(basePackages = "com.atlas.common.infra.web.exception")
public class WebExceptionHandlerConfig {}
