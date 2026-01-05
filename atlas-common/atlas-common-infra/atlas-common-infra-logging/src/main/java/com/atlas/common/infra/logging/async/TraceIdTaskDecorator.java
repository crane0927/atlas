/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.logging.async;

import com.atlas.common.infra.logging.trace.TraceIdUtil;
import org.springframework.core.task.TaskDecorator;

/**
 * TraceId 异步任务装饰器
 *
 * <p>确保异步任务继承父线程的 TraceId，实现异步任务日志可追溯。
 *
 * <p>工作流程：
 *
 * <ol>
 *   <li>异步任务执行前，从父线程获取 TraceId
 *   <li>在新线程中设置 TraceId 到 ThreadLocal 和 MDC
 *   <li>异步任务执行完成后，清理 TraceId（由任务执行器负责）
 * </ol>
 *
 * <p>配置示例：
 *
 * <pre>{@code
 * @Configuration
 * @EnableAsync
 * public class AsyncConfig {
 *     @Bean
 *     public Executor taskExecutor() {
 *         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 *         executor.setCorePoolSize(10);
 *         executor.setTaskDecorator(new TraceIdTaskDecorator());
 *         executor.initialize();
 *         return executor;
 *     }
 * }
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class TraceIdTaskDecorator implements TaskDecorator {

  /**
   * 装饰任务
   *
   * <p>从父线程获取 TraceId，并在新线程中设置，确保异步任务日志可追溯。
   *
   * @param runnable 原始任务
   * @return 装饰后的任务
   */
  @Override
  public Runnable decorate(Runnable runnable) {
    // 从父线程获取 TraceId
    String traceId = TraceIdUtil.getTraceId();

    // 返回装饰后的任务，在新线程中设置 TraceId
    return () -> {
      try {
        // 在新线程中设置 TraceId
        if (traceId != null && !traceId.isEmpty()) {
          TraceIdUtil.setTraceId(traceId);
        }
        // 执行原始任务
        runnable.run();
      } finally {
        // 清理 TraceId，避免内存泄漏
        TraceIdUtil.clear();
      }
    };
  }
}
