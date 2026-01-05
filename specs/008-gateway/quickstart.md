# 快速开始指南

## 概述

本指南介绍如何创建和使用 `atlas-gateway` 模块，实现 API 网关功能，包括路由转发、CORS 跨域支持、TraceId 链路追踪、统一错误返回、鉴权控制等功能。

## 前置条件

- JDK 21
- Spring Boot 4.0.1
- Spring Cloud 2025.1.0
- Spring Cloud Alibaba 2025.1.0
- Maven 3.8+
- Nacos Server（用于配置管理）
- 已安装 `atlas-common-feature-core` 模块（用于统一响应格式）
- 已安装 `atlas-common-infra-logging` 模块（用于 TraceId 管理）

## 创建 Gateway 模块

### 1. 创建模块目录结构

```
atlas/
└── atlas-gateway/
    ├── pom.xml
    ├── README.md
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── com/atlas/gateway/
        │   │       ├── config/
        │   │       ├── filter/
        │   │       ├── exception/
        │   │       └── GatewayApplication.java
        │   └── resources/
        │       └── application.yml
        └── test/
            └── java/
                └── com/atlas/gateway/
```

### 2. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.atlas</groupId>
        <artifactId>atlas</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>atlas-gateway</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Spring Cloud Gateway -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>

        <!-- Nacos Config -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>

        <!-- Nacos Discovery (可选) -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <!-- Atlas Common Feature Core -->
        <dependency>
            <groupId>com.atlas</groupId>
            <artifactId>atlas-common-feature-core</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- Atlas Common Infra Logging -->
        <dependency>
            <groupId>com.atlas</groupId>
            <artifactId>atlas-common-infra-logging</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- Spring Boot Starter Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### 3. 创建主类

创建 `GatewayApplication.java`：

```java
package com.atlas.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Gateway 应用主类
 *
 * @author Atlas Team
 */
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

### 4. 配置 application.yml

创建 `application.yml`：

```yaml
spring:
  application:
    name: atlas-gateway
  cloud:
    gateway:
      # Gateway 路由配置（也可以通过 Nacos Config 配置）
      routes:
        - id: health-route
          uri: http://localhost:8080
          predicates:
            - Path=/health/**
          filters:
            - StripPrefix=1
        - id: mock-route
          uri: http://localhost:8080
          predicates:
            - Path=/mock/**
          filters:
            - StripPrefix=1

    # Nacos Config 配置
    cloud:
      nacos:
        config:
          server-addr: localhost:8848
          file-extension: yaml
          group: DEFAULT_GROUP
          namespace: dev
          data-id: atlas-gateway-dev.yaml

# Atlas Gateway 配置
atlas:
  gateway:
    # 白名单配置
    whitelist:
      enabled: true
      paths:
        - /health/**
        - /mock/**
        - /api/public/**
    
    # CORS 配置
    cors:
      allowed-origins: "*"
      allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600

# 日志配置
logging:
  level:
    root: INFO
    com.atlas.gateway: DEBUG
```

### 5. 配置 Nacos Config

在 Nacos 控制台创建配置：

**DataId**: `atlas-gateway-dev.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置格式**: YAML

```yaml
atlas:
  gateway:
    routes:
      - id: health-route
        uri: http://localhost:8080
        predicates:
          - Path=/health/**
        filters:
          - StripPrefix=1
      - id: mock-route
        uri: http://localhost:8080
        predicates:
          - Path=/mock/**
        filters:
          - StripPrefix=1
    
    whitelist:
      enabled: true
      paths:
        - /health/**
        - /mock/**
        - /api/public/**
    
    cors:
      allowed-origins: "*"
      allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600
```

## 使用示例

### 1. 路由转发

Gateway 会自动根据路由规则转发请求到对应的后端服务。

**示例请求**:
```bash
# 转发到 /health 接口
curl http://localhost:8080/gateway/health

# 转发到 /mock 接口
curl http://localhost:8080/gateway/mock/test
```

### 2. TraceId 传递

Gateway 会自动处理 TraceId，确保所有请求都有 TraceId。

**示例请求**:
```bash
# 不带 TraceId 的请求（Gateway 会自动生成）
curl http://localhost:8080/gateway/health

# 带 TraceId 的请求（Gateway 会使用请求头中的 TraceId）
curl -H "X-Trace-Id: abc123def456" http://localhost:8080/gateway/health
```

**响应头**:
```
X-Trace-Id: abc123def456
```

### 3. CORS 跨域支持

Gateway 会自动处理 CORS 跨域请求。

**示例请求**:
```bash
# 预检请求（OPTIONS）
curl -X OPTIONS http://localhost:8080/gateway/health \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET"
```

**响应头**:
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
Access-Control-Allow-Headers: *
Access-Control-Max-Age: 3600
```

### 4. 统一错误返回

Gateway 的所有错误响应使用统一的 `Result` 格式。

**示例错误响应**:
```json
{
  "code": "010404",
  "message": "路由不存在",
  "data": null,
  "timestamp": 1706342400000,
  "traceId": "abc123def456"
}
```

### 5. 白名单功能

白名单路径的请求可以直接通过 Gateway，无需 Token 校验。

**示例请求**:
```bash
# 白名单路径（无需 Token）
curl http://localhost:8080/gateway/health

# 非白名单路径（会触发 Token 校验，占位实现默认放行）
curl http://localhost:8080/gateway/api/user/info
```

## 配置说明

### 路由配置

路由配置支持通过 `application.yml` 和 Nacos Config 两种方式配置。推荐使用 Nacos Config 配置，支持动态更新。

**配置项说明**:
- `id`: 路由 ID，唯一标识
- `uri`: 后端服务 URI
- `predicates`: 路由断言（匹配条件），如 `Path=/health/**`
- `filters`: 路由过滤器（路径重写等），如 `StripPrefix=1`

### 白名单配置

白名单配置支持通过 Nacos Config 动态更新。

**配置项说明**:
- `enabled`: 是否启用白名单
- `paths`: 白名单路径列表（支持通配符）

### CORS 配置

CORS 配置支持通过 Nacos Config 动态更新。

**配置项说明**:
- `allowed-origins`: 允许的源（多个用逗号分隔，* 表示所有）
- `allowed-methods`: 允许的 HTTP 方法（多个用逗号分隔）
- `allowed-headers`: 允许的请求头（多个用逗号分隔，* 表示所有）
- `allow-credentials`: 是否允许携带凭证
- `max-age`: 预检请求缓存时间（秒）

## 扩展 Token 校验

Gateway 提供 Token 校验扩展点，允许后续扩展实现具体的 Token 校验逻辑。

**实现方式**:
```java
@Component
public class CustomTokenValidator implements TokenValidator {
    @Override
    public boolean validate(ServerHttpRequest request) {
        // 实现具体的 Token 校验逻辑
        String token = request.getHeaders().getFirst("Authorization");
        // 校验 Token
        return isValid(token);
    }
}
```

## 验收测试

### 1. 健康检查接口测试

```bash
# 测试 Gateway 能否转发请求到临时的 health/mock 接口
curl http://localhost:8080/gateway/health
curl http://localhost:8080/gateway/mock/test
```

### 2. TraceId 传递测试

```bash
# 测试 TraceId 是否在请求头和响应头中传递
curl -v http://localhost:8080/gateway/health
# 检查响应头中的 X-Trace-Id
```

### 3. 统一错误码测试

```bash
# 测试不存在的路由，应该返回统一错误格式
curl http://localhost:8080/gateway/not-exist
# 应该返回 Result 格式的错误响应，错误码为 010404
```

### 4. 白名单测试

```bash
# 测试白名单路径（应该能正常通过）
curl http://localhost:8080/gateway/health

# 测试非白名单路径（会触发 Token 校验，占位实现默认放行）
curl http://localhost:8080/gateway/api/user/info
```

### 5. 配置更新测试

1. 在 Nacos 控制台修改路由规则或白名单配置
2. 等待配置生效（< 5 秒）
3. 测试配置是否生效

## 常见问题

### Q1: Gateway 无法启动？

**A**: 检查以下几点：
- 确认 Nacos Server 是否启动
- 确认 Nacos Config 配置是否正确
- 确认依赖是否正确添加

### Q2: 路由转发失败？

**A**: 检查以下几点：
- 确认路由规则配置是否正确
- 确认后端服务是否启动
- 确认后端服务 URI 是否正确

### Q3: TraceId 没有传递？

**A**: 检查以下几点：
- 确认 `TraceIdGatewayFilter` 是否正确配置
- 确认 `TraceIdUtil` 是否正确集成
- 检查日志中是否有 TraceId

### Q4: CORS 跨域请求失败？

**A**: 检查以下几点：
- 确认 CORS 配置是否正确
- 确认预检请求（OPTIONS）是否正确处理
- 检查浏览器控制台的错误信息

### Q5: 配置更新不生效？

**A**: 检查以下几点：
- 确认 Nacos Config 配置变更监听是否正确实现
- 确认配置刷新机制是否正确配置
- 检查日志中是否有配置更新记录

## 参考资源

- [Spring Cloud Gateway 官方文档](https://spring.io/projects/spring-cloud-gateway)
- [Nacos Config 官方文档](https://nacos.io/docs/latest/guide/user/quick-start/)
- [项目配置命名规范](../../../docs/engineering-standards/config-naming.md)
- [项目错误码规范](../../../docs/engineering-standards/error-code.md)

