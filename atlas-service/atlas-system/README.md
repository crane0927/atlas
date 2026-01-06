# Atlas System Service

## 模块简介

`atlas-system` 是 Atlas 项目的系统服务模块，提供用户、角色、权限管理的最小闭环功能。服务实现 `atlas-system-api` 中定义的接口契约，支持 `atlas-auth` 服务的用户认证和权限授权需求。

## 主要功能

### 1. 用户管理

- **用户查询**: 根据用户ID或用户名查询用户信息
- **用户创建**: 创建新用户（最小闭环功能）

### 2. 角色管理

- **角色查询**: 查询用户角色列表
- **角色创建**: 创建新角色（最小闭环功能）

### 3. 权限管理

- **权限查询**: 查询用户权限列表（通过角色关联）
- **权限创建**: 创建新权限（最小闭环功能）

### 4. 关联管理

- **用户角色关联**: 为用户分配角色
- **角色权限关联**: 为角色分配权限

## 快速开始

### 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-system</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置

在 `application.yml` 中配置：

```yaml
spring:
  application:
    name: atlas-system
  datasource:
    url: jdbc:postgresql://localhost:5432/atlas_system
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
        group: DEV_GROUP
      config:
        server-addr: localhost:8848
        namespace: dev
        group: DEV_GROUP
        file-extension: yaml

server:
  port: 8081
```

### 使用示例

#### 1. 查询用户信息

```java
// Auth 服务调用
GET /api/v1/users/1
GET /api/v1/users/by-username?username=admin

// 响应
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@example.com",
    "phone": "13800138000",
    "status": "ACTIVE",
    "avatar": "https://example.com/avatar.jpg"
  }
}
```

#### 2. 查询用户权限

```java
// Auth 服务调用
GET /api/v1/users/1/roles
GET /api/v1/users/1/permissions
GET /api/v1/users/1/authorities

// 响应
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "userId": 1,
    "roles": ["ADMIN", "USER"],
    "permissions": ["user:read", "user:write", "user:delete"]
  }
}
```

#### 3. 创建用户

```java
// 管理接口调用
POST /api/v1/users
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "nickname": "新用户",
  "email": "newuser@example.com"
}
```

## API 接口

### 查询接口（实现 atlas-system-api）

1. **用户查询**
   - `GET /api/v1/users/{userId}` - 根据用户ID查询用户信息
   - `GET /api/v1/users/by-username?username={username}` - 根据用户名查询用户信息

2. **权限查询**
   - `GET /api/v1/users/{userId}/roles` - 查询用户角色列表
   - `GET /api/v1/users/{userId}/permissions` - 查询用户权限列表
   - `GET /api/v1/users/{userId}/authorities` - 查询用户完整权限信息

### 管理接口（最小闭环）

1. **用户管理**
   - `POST /api/v1/users` - 创建用户

2. **角色管理**
   - `POST /api/v1/roles` - 创建角色

3. **权限管理**
   - `POST /api/v1/permissions` - 创建权限

4. **关联管理**
   - `POST /api/v1/users/{userId}/roles` - 为用户分配角色
   - `POST /api/v1/roles/{roleId}/permissions` - 为角色分配权限

## 依赖关系

- `atlas-system-api`: API 接口定义（Feign 接口契约、DTO、枚举）
- `atlas-common-infra-db`: MyBatis-Plus 配置、数据库基础设施
- `atlas-common-feature-core`: 统一响应格式、异常处理、错误码
- `spring-cloud-starter-openfeign`: Feign 客户端
- `mybatis-plus-boot-starter`: MyBatis-Plus 数据访问
- `postgresql`: PostgreSQL 数据库驱动
- `flyway-core`: 数据库迁移工具

## 数据库

### 表结构

- `sys_user`: 用户表
- `sys_role`: 角色表
- `sys_permission`: 权限表
- `sys_user_role`: 用户角色关联表
- `sys_role_permission`: 角色权限关联表

### 迁移脚本

- Flyway 迁移脚本：`src/main/resources/db/migration/`
- SQL 脚本目录：`sql/v1.0.0/`（按版本管理）

## 注意事项

1. **数据实时生效**: 数据保存到数据库后立即生效，无需重启服务或刷新缓存
2. **接口契约**: 查询接口必须严格按照 `atlas-system-api` 中定义的接口实现
3. **包结构**: 遵循业务模块按业务再按技术分层组织（`com.atlas.system.user.controller`）
4. **SQL 目录**: 在服务目录下建立 `sql` 目录，按版本管理 SQL 脚本

## 参考资源

- [API 契约定义](../../specs/011-system-service/contracts/README.md)
- [数据模型定义](../../specs/011-system-service/data-model.md)
- [快速开始指南](../../specs/011-system-service/quickstart.md)
- [技术调研文档](../../specs/011-system-service/research.md)

