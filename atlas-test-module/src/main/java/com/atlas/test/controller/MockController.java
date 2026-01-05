/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.test.controller;

import com.atlas.common.feature.core.result.Result;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock 控制器
 *
 * <p>提供临时的 health/mock 接口用于 Gateway 验收测试。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping
public class MockController {

  /**
   * 健康检查接口
   *
   * @return 健康检查响应
   */
  @GetMapping("/health")
  public Result<HealthResponse> health() {
    HealthResponse response = new HealthResponse();
    response.setStatus("UP");
    response.setMessage("Gateway health check passed");
    return Result.success(response);
  }

  /**
   * Mock 测试接口
   *
   * @return Mock 响应
   */
  @GetMapping("/mock/test")
  public Result<MockResponse> mockTest() {
    MockResponse response = new MockResponse();
    response.setMessage("Mock test endpoint");
    response.setTimestamp(System.currentTimeMillis());
    return Result.success(response);
  }

  /** 健康检查响应 */
  @Data
  public static class HealthResponse {
    private String status;
    private String message;
  }

  /** Mock 响应 */
  @Data
  public static class MockResponse {
    private String message;
    private Long timestamp;
  }
}

