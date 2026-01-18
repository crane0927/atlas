/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.web.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.atlas.common.infra.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

/**
 * WebExceptionAutoConfiguration 单元测试
 *
 * @author Atlas Team
 * @since 1.0.0
 */
class WebExceptionAutoConfigurationTest {

  private final WebApplicationContextRunner contextRunner =
      new WebApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(WebExceptionAutoConfiguration.class));

  @Test
  void shouldRegisterGlobalExceptionHandler() {
    contextRunner.run(
        context -> assertThat(context).hasSingleBean(GlobalExceptionHandler.class));
  }
}
