# 配置命名规范

## 概述

本文档定义了 Atlas 项目在 Nacos 配置中心中的配置命名规范，确保所有模块的配置命名统一、清晰、易于管理。

## DataId 命名规则

### 命名格式

DataId 必须遵循以下格式：

```
{application-name}-{profile}.{extension}
```

### 格式说明

| 部分 | 说明 | 示例 |
|------|------|------|
| `{application-name}` | 应用名称，与 Spring Boot 应用名一致 | `atlas-system`、`atlas-gateway` |
| `{profile}` | 环境标识 | `dev`、`test`、`prod` |
| `{extension}` | 配置文件扩展名 | `yaml`、`yml`、`properties` |

### 命名示例

| 应用 | 环境 | DataId |
|------|------|--------|
| atlas-system | dev | `atlas-system-dev.yaml` |
| atlas-system | test | `atlas-system-test.yaml` |
| atlas-system | prod | `atlas-system-prod.yaml` |
| atlas-gateway | dev | `atlas-gateway-dev.yaml` |
| atlas-auth | dev | `atlas-auth-dev.yaml` |

### 命名规则

1. **应用名称**: 必须与 Spring Boot `spring.application.name` 配置一致
2. **环境标识**: 使用小写字母，标准环境为 `dev`、`test`、`prod`
3. **扩展名**: 推荐使用 `yaml`（更易读），也支持 `yml` 和 `properties`
4. **小写字母**: DataId 全部使用小写字母和连字符

## Group 命名规则

### 命名策略

Group 采用**按环境分组**的策略，便于配置管理和权限控制。

### 标准 Group 名称

| 环境 | Group 名称 | 说明 |
|------|-----------|------|
| 开发环境 | `DEV_GROUP` | 开发环境配置组 |
| 测试环境 | `TEST_GROUP` | 测试环境配置组 |
| 生产环境 | `PROD_GROUP` | 生产环境配置组 |
| 默认 | `DEFAULT_GROUP` | 默认配置组（通常用于开发环境） |

### Group 使用建议

1. **开发环境**: 使用 `DEV_GROUP` 或 `DEFAULT_GROUP`
2. **测试环境**: 使用 `TEST_GROUP`
3. **生产环境**: 使用 `PROD_GROUP`，并设置严格的权限控制

### Group 命名示例

```
DataId: atlas-system-dev.yaml
Group: DEV_GROUP

DataId: atlas-system-prod.yaml
Group: PROD_GROUP
```

## 配置项 Key 命名规则

### 命名格式

配置项 Key 必须遵循以下层级结构：

```
{module}.{category}.{key}
```

### 格式说明

| 部分 | 说明 | 示例 |
|------|------|------|
| `{module}` | 模块标识，通常为 `atlas.{module-name}` | `atlas.system`、`atlas.gateway` |
| `{category}` | 配置分类 | `database`、`redis`、`jwt`、`rate-limit` |
| `{key}` | 具体的配置项名称 | `url`、`username`、`secret`、`enabled` |

### 命名示例

| 模块 | 分类 | Key | 完整 Key | 说明 |
|------|------|-----|----------|------|
| atlas.system | database | url | `atlas.system.database.url` | 数据库连接地址 |
| atlas.system | database | username | `atlas.system.database.username` | 数据库用户名 |
| atlas.gateway | rate-limit | enabled | `atlas.gateway.rate-limit.enabled` | 限流开关 |
| atlas.gateway | rate-limit | qps | `atlas.gateway.rate-limit.qps` | 限流 QPS |
| atlas.auth | jwt | secret | `atlas.auth.jwt.secret` | JWT 密钥 |
| atlas.auth | jwt | expire | `atlas.auth.jwt.expire` | JWT 过期时间 |

### 配置分类

常用配置分类：

| 分类 | 说明 | 示例 Key |
|------|------|----------|
| `database` | 数据库配置 | `atlas.system.database.url` |
| `redis` | Redis 配置 | `atlas.system.redis.host` |
| `jwt` | JWT 配置 | `atlas.auth.jwt.secret` |
| `rate-limit` | 限流配置 | `atlas.gateway.rate-limit.enabled` |
| `logging` | 日志配置 | `atlas.system.logging.level` |
| `nacos` | Nacos 配置 | `atlas.system.nacos.server-addr` |
| `sentinel` | Sentinel 配置 | `atlas.system.sentinel.dashboard` |

## 配置示例

### 完整配置示例

**DataId**: `atlas-system-dev.yaml`  
**Group**: `DEV_GROUP`

```yaml
# 数据库配置
atlas:
  system:
    database:
      url: jdbc:postgresql://localhost:5432/atlas_system
      username: atlas
      password: atlas123
      driver-class-name: org.postgresql.Driver
    
    # Redis 配置
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
      timeout: 3000
    
    # 日志配置
    logging:
      level:
        root: DEBUG
        com.atlas: DEBUG
```

**DataId**: `atlas-gateway-dev.yaml`  
**Group**: `DEV_GROUP`

```yaml
atlas:
  gateway:
    # 限流配置
    rate-limit:
      enabled: true
      qps: 1000
      burst: 2000
    
    # 跨域配置
    cors:
      allowed-origins: "*"
      allowed-methods: GET,POST,PUT,DELETE
      allowed-headers: "*"
```

**DataId**: `atlas-auth-dev.yaml`  
**Group**: `DEV_GROUP`

```yaml
atlas:
  auth:
    # JWT 配置
    jwt:
      secret: atlas-jwt-secret-key-2025
      expire: 7200
      refresh-expire: 604800
    
    # Token 配置
    token:
      header: Authorization
      prefix: Bearer 
```

## Spring Boot 配置绑定

### 使用 @ConfigurationProperties

```java
package com.atlas.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统配置属性
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
@Data
@Component
@ConfigurationProperties(prefix = "atlas.system")
public class SystemProperties {
    
    private Database database = new Database();
    private Redis redis = new Redis();
    
    @Data
    public static class Database {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }
    
    @Data
    public static class Redis {
        private String host;
        private Integer port;
        private String password;
        private Integer database;
        private Integer timeout;
    }
}
```

### 使用 @Value

```java
package com.atlas.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GatewayConfig {
    
    @Value("${atlas.gateway.rate-limit.enabled:false}")
    private Boolean rateLimitEnabled;
    
    @Value("${atlas.gateway.rate-limit.qps:1000}")
    private Integer rateLimitQps;
}
```

## Nacos 配置管理

### 配置创建流程

1. **登录 Nacos 控制台**: 访问 Nacos 管理界面
2. **选择命名空间**: 选择对应的环境命名空间
3. **创建配置**:
   - DataId: 按照命名规范填写（如 `atlas-system-dev.yaml`）
   - Group: 选择对应的 Group（如 `DEV_GROUP`）
   - 配置格式: 选择 `YAML` 或 `Properties`
   - 配置内容: 填写配置内容
4. **发布配置**: 点击发布，配置立即生效

### 配置版本管理

1. **配置历史**: Nacos 自动保存配置历史版本
2. **配置回滚**: 可以回滚到历史版本
3. **配置对比**: 可以对比不同版本的配置差异

### 配置权限控制

1. **命名空间隔离**: 不同环境使用不同的命名空间
2. **Group 权限**: 可以为不同 Group 设置不同的访问权限
3. **配置加密**: 敏感配置（如密码、密钥）应加密存储

## 配置命名检查

### 检查清单

在创建或修改配置时，检查以下事项：

- [ ] DataId 符合命名格式：`{application-name}-{profile}.{extension}`
- [ ] Group 使用标准 Group 名称
- [ ] 配置项 Key 使用层级结构：`{module}.{category}.{key}`
- [ ] 所有 Key 使用小写字母和连字符
- [ ] 配置内容格式正确（YAML 缩进、Properties 格式等）

### 工具检查

可以使用以下方式检查配置命名：

1. **代码审查**: 在代码审查时检查配置命名
2. **配置扫描**: 使用脚本扫描 Nacos 配置，检查命名规范
3. **CI/CD 检查**: 在 CI/CD 流水线中添加配置命名检查

## 配置迁移指南

### 从旧规范迁移

如果现有配置不符合新规范，按以下步骤迁移：

1. **识别配置**: 列出所有需要迁移的配置
2. **规划新命名**: 按照新规范规划新的 DataId、Group 和 Key
3. **创建新配置**: 在 Nacos 中创建新配置
4. **更新应用**: 更新应用配置，指向新的配置
5. **验证功能**: 验证应用功能正常
6. **删除旧配置**: 确认无误后删除旧配置

### 迁移示例

**旧配置**:
- DataId: `system-config.properties`
- Group: `DEFAULT_GROUP`
- Key: `db.url`

**新配置**:
- DataId: `atlas-system-dev.yaml`
- Group: `DEV_GROUP`
- Key: `atlas.system.database.url`

## 常见问题

### Q1: 如何为不同环境创建配置？

**A**: 使用不同的 DataId 和 Group：
- 开发环境: `atlas-system-dev.yaml`，Group: `DEV_GROUP`
- 测试环境: `atlas-system-test.yaml`，Group: `TEST_GROUP`
- 生产环境: `atlas-system-prod.yaml`，Group: `PROD_GROUP`

### Q2: 配置项 Key 可以有多深？

**A**: 建议不超过 4 层（如 `atlas.system.database.url`），过深会影响可读性。

### Q3: 如何管理共享配置？

**A**: 可以创建共享配置，使用通用的 DataId（如 `atlas-common-dev.yaml`），多个应用引用。

### Q4: 敏感配置如何管理？

**A**: 
1. 使用 Nacos 的配置加密功能
2. 使用环境变量覆盖敏感配置
3. 使用密钥管理服务（如 Vault）

## 参考

- [Nacos 配置管理文档](https://nacos.io/docs/latest/guide/user/configuration/)
- [Spring Boot 外部化配置](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [项目宪法 - 组件优先使用 Spring Cloud 生态](.specify/memory/constitution.md)

