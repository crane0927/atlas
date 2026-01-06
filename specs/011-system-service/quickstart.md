# 快速开始指南

## 概述

本指南介绍如何快速启动和使用 `atlas-system` 服务，包括环境准备、配置、启动和基本使用。

## 前置条件

- JDK 21
- Maven 3.8+
- PostgreSQL 12+（本地或远程）
- Nacos 2.0+（用于服务注册与发现）

## 环境准备

### 1. 数据库准备

创建 PostgreSQL 数据库：

```sql
CREATE DATABASE atlas_system;
```

### 2. Nacos 准备

启动 Nacos 服务器（默认端口 8848）：

```bash
# 使用 Docker 启动 Nacos
docker run -d \
  --name nacos \
  -p 8848:8848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.3.0
```

或使用本地安装的 Nacos。

## 配置

### 1. 数据库配置

在 `application.yml` 中配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/atlas_system
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
```

### 2. Nacos 配置

在 `application.yml` 中配置 Nacos：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: atlas
        group: DEFAULT_GROUP
      config:
        server-addr: localhost:8848
        namespace: atlas
        group: DEFAULT_GROUP
        file-extension: yaml
```

### 3. Flyway 配置

在 `application.yml` 中配置 Flyway：

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

## 启动服务

### 1. 编译项目

```bash
mvn clean install
```

### 2. 启动服务

```bash
cd atlas-system
mvn spring-boot:run
```

或使用 IDE 运行 `SystemApplication` 主类。

### 3. 验证启动

检查服务是否成功启动：

1. **日志检查**: 查看控制台日志，确认服务启动成功
2. **Nacos 检查**: 登录 Nacos 控制台（http://localhost:8848/nacos），确认服务已注册
3. **健康检查**: 访问 `http://localhost:8080/actuator/health`（如果配置了 Actuator）

## 数据库初始化

### 自动初始化

Flyway 会在服务启动时自动执行数据库迁移脚本，创建所有必要的表结构。

### 手动初始化（可选）

如果需要手动执行 SQL 脚本，可以：

1. 进入 `sql/v1.0.0/` 目录
2. 按照 `README.md` 中的说明顺序执行 SQL 脚本
3. 或使用 Flyway 命令行工具执行

## 基本使用

### 1. 创建用户

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "nickname": "管理员",
    "email": "admin@example.com"
  }'
```

### 2. 查询用户

```bash
# 根据用户ID查询
curl http://localhost:8080/api/v1/users/1

# 根据用户名查询
curl http://localhost:8080/api/v1/users/by-username?username=admin
```

### 3. 创建角色

```bash
curl -X POST http://localhost:8080/api/v1/roles \
  -H "Content-Type: application/json" \
  -d '{
    "roleCode": "ADMIN",
    "roleName": "管理员",
    "description": "系统管理员"
  }'
```

### 4. 创建权限

```bash
curl -X POST http://localhost:8080/api/v1/permissions \
  -H "Content-Type: application/json" \
  -d '{
    "permissionCode": "user:read",
    "permissionName": "用户查询",
    "description": "查询用户信息"
  }'
```

### 5. 分配角色

```bash
# 为用户分配角色
curl -X POST http://localhost:8080/api/v1/users/1/roles \
  -H "Content-Type: application/json" \
  -d '{
    "roleId": 1
  }'
```

### 6. 分配权限

```bash
# 为角色分配权限
curl -X POST http://localhost:8080/api/v1/roles/1/permissions \
  -H "Content-Type: application/json" \
  -d '{
    "permissionId": 1
  }'
```

### 7. 查询权限

```bash
# 查询用户角色
curl http://localhost:8080/api/v1/users/1/roles

# 查询用户权限
curl http://localhost:8080/api/v1/users/1/permissions

# 查询用户完整权限信息
curl http://localhost:8080/api/v1/users/1/authorities
```

## 与 Auth 服务集成

### 1. 配置 Feign 客户端

在 `atlas-auth` 服务中，确保已配置 Feign 客户端：

```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.atlas.system.api.v1.feign")
public class AuthApplication {
    // ...
}
```

### 2. 验证集成

启动 Auth 服务，验证能够成功调用 System 服务：

```bash
# 在 Auth 服务中测试用户查询
curl http://localhost:8081/api/v1/auth/test-user-query
```

## 常见问题

### 1. 服务无法启动

**问题**: 服务启动失败，提示数据库连接错误

**解决方案**:
- 检查数据库是否已启动
- 检查数据库连接配置是否正确
- 检查数据库用户权限

### 2. 服务无法注册到 Nacos

**问题**: 服务启动成功，但 Nacos 中看不到服务

**解决方案**:
- 检查 Nacos 是否已启动
- 检查 Nacos 配置是否正确
- 检查网络连接

### 3. 数据库迁移失败

**问题**: Flyway 迁移脚本执行失败

**解决方案**:
- 检查 SQL 脚本语法是否正确
- 检查数据库用户是否有执行权限
- 查看 Flyway 日志了解具体错误

### 4. 接口调用失败

**问题**: Auth 服务无法调用 System 服务

**解决方案**:
- 检查 System 服务是否已启动
- 检查服务是否已注册到 Nacos
- 检查 Feign 配置是否正确
- 检查网络连接

## 下一步

- 查看 [数据模型文档](data-model.md) 了解数据模型设计
- 查看 [规划文档](plan.md) 了解实施计划
- 查看 [API 合约文档](contracts/README.md) 了解接口定义

## 参考资源

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [Flyway 官方文档](https://flywaydb.org/documentation/)
- [Nacos 官方文档](https://nacos.io/docs/latest/what-is-nacos/)

