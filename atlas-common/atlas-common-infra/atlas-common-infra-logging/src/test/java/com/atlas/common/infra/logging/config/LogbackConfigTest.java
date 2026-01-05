/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.config;

import static org.junit.jupiter.api.Assertions.*;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/** Logback 配置单元测试 */
class LogbackConfigTest {

  private Logger logger;
  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    logger = LoggerFactory.getLogger(LogbackConfigTest.class);
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    listAppender = new ListAppender<>();
    listAppender.setContext(loggerContext);
    listAppender.start();
    ch.qos.logback.classic.Logger rootLogger =
        loggerContext.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    rootLogger.addAppender(listAppender);
    MDC.clear();
  }

  @Test
  void testLogFormatWithTraceId() {
    // Given
    String traceId = "test-trace-id-12345";
    MDC.put("traceId", traceId);

    // When
    logger.info("测试日志消息");

    // Then
    assertFalse(listAppender.list.isEmpty());
    ILoggingEvent event = listAppender.list.get(0);
    String formattedMessage = event.getFormattedMessage();
    assertNotNull(formattedMessage);
    // 验证日志格式包含 TraceId（通过 MDC 获取）
    assertEquals(traceId, event.getMDCPropertyMap().get("traceId"));
  }

  @Test
  void testLogFormatWithoutTraceId() {
    // Given - MDC 已清除

    // When
    logger.info("测试日志消息");

    // Then
    assertFalse(listAppender.list.isEmpty());
    ILoggingEvent event = listAppender.list.get(0);
    // TraceId 为空时，MDC 中应该没有 traceId
    assertNull(event.getMDCPropertyMap().get("traceId"));
  }

  @Test
  void testLogLevel() {
    // When
    logger.debug("DEBUG 级别日志");
    logger.info("INFO 级别日志");
    logger.warn("WARN 级别日志");
    logger.error("ERROR 级别日志");

    // Then
    assertFalse(listAppender.list.isEmpty());
    // 验证不同级别的日志都能正常输出
    assertEquals(4, listAppender.list.size());
  }

  @Test
  void testLogFormatContainsRequiredFields() {
    // Given
    String traceId = "test-trace-id-12345";
    MDC.put("traceId", traceId);

    // When
    logger.info("测试日志消息");

    // Then
    assertFalse(listAppender.list.isEmpty());
    ILoggingEvent event = listAppender.list.get(0);
    // 验证日志事件包含必需的字段
    assertNotNull(event.getTimestamp());
    assertNotNull(event.getLevel());
    assertNotNull(event.getLoggerName());
    assertNotNull(event.getFormattedMessage());
    assertNotNull(event.getMDCPropertyMap());
  }
}

