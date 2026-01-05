/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.logging.async;

import static org.junit.jupiter.api.Assertions.*;

import com.atlas.common.infra.logging.trace.TraceIdUtil;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/** TraceIdTaskDecorator 单元测试 */
class TraceIdTaskDecoratorTest {

  private TraceIdTaskDecorator decorator;

  @BeforeEach
  void setUp() {
    decorator = new TraceIdTaskDecorator();
    TraceIdUtil.clear();
    MDC.clear();
  }

  @AfterEach
  void tearDown() {
    TraceIdUtil.clear();
    MDC.clear();
  }

  @Test
  void testDecorateWithTraceId() throws InterruptedException {
    // Given
    String traceId = "test-trace-id-12345";
    TraceIdUtil.setTraceId(traceId);
    AtomicReference<String> asyncTraceId = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);

    Runnable originalTask =
        () -> {
          asyncTraceId.set(TraceIdUtil.getTraceId());
          latch.countDown();
        };

    // When
    Runnable decoratedTask = decorator.decorate(originalTask);
    Thread thread = new Thread(decoratedTask);
    thread.start();
    latch.await();

    // Then
    assertEquals(traceId, asyncTraceId.get());
    // 主线程的 TraceId 不受影响
    assertEquals(traceId, TraceIdUtil.getTraceId());
  }

  @Test
  void testDecorateWithoutTraceId() throws InterruptedException {
    // Given - TraceId 未设置
    AtomicReference<String> asyncTraceId = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);

    Runnable originalTask =
        () -> {
          asyncTraceId.set(TraceIdUtil.getTraceId());
          latch.countDown();
        };

    // When
    Runnable decoratedTask = decorator.decorate(originalTask);
    Thread thread = new Thread(decoratedTask);
    thread.start();
    latch.await();

    // Then
    assertNull(asyncTraceId.get());
  }

  @Test
  void testDecorateCleansUpAfterExecution() throws InterruptedException {
    // Given
    String traceId = "test-trace-id-12345";
    TraceIdUtil.setTraceId(traceId);
    AtomicReference<String> afterExecutionTraceId = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);

    Runnable originalTask =
        () -> {
          // 任务执行中应该有 TraceId
          assertNotNull(TraceIdUtil.getTraceId());
          latch.countDown();
          // 等待一小段时间，确保清理逻辑执行
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          afterExecutionTraceId.set(TraceIdUtil.getTraceId());
        };

    // When
    Runnable decoratedTask = decorator.decorate(originalTask);
    Thread thread = new Thread(decoratedTask);
    thread.start();
    latch.await();
    thread.join();

    // Then - 异步线程中的 TraceId 应该被清理
    assertNull(afterExecutionTraceId.get());
  }
}

