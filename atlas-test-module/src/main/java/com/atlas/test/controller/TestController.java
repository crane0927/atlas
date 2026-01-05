/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.test.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author Atlas Team
 * @date 2025-01-27
 */
@RestController
@RequestMapping("/test")
public class TestController {

  @GetMapping("/hello")
  public TestResponse hello() {
    return new TestResponse("Hello, Atlas!");
  }

  @Data
  public static class TestResponse {
    private String message;

    public TestResponse(String message) {
      this.message = message;
    }
  }
}
