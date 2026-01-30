# atlas-system

## 模块简介

`atlas-system` 是 Atlas 项目的系统服务模块，提供用户、角色、权限管理功能。服务实现 `atlas-system-api` 中定义的接口契约，支持 `atlas-auth` 服务的用户认证和权限授权需求。

## 主要功能

### 1. 用户管理

- **用户查询**: 根据用户ID或用户名查询用户信息
- **用户创建**: 创建新用户，密码自动加密存储
- **用户角色关联**: 为用户分配角色

### 2. 角色管理

- **角色查询**: 查询用户角色列表
- **角色创建**: 创建新角色
- **角色权限关联**: 为角色分配权限

### 3. 权限管理

- **权限查询**: 查询用户权限列表（通过角色关联）
- **权限创建**: 创建新权限
- **完整权限信息**: 查询用户完整权限信息（角色+权限）

### 4. 关联管理

- **用户角色关联**: 为用户分配角色
- **角色权限关联**: 为角色分配权限

## 快速开始

### 前置要求

- JDK 21
- Maven 3.8+
- PostgreSQL 数据库
- Nacos 配置中心和服务发现

### 配置数据库

在 PostgreSQL 中创建数据库与 schema：

```sql
CREATE DATABASE atlas;
CREATE SCHEMA atlas_system;
```

### 配置应用

在 `application.yml` 或 Nacos Config 中配置：

```yaml
spring:
  application:
    name: atlas-system
  config:
    import: optional:nacos:atlas-system-dev.yaml
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        username: nacos
        password: nacos
        namespace: ${NACOS_NAMESPACE:dev}
        group: ${NACOS_GROUP:DEV_GROUP}
      config:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        username: nacos
        password: nacos
        namespace: ${NACOS_NAMESPACE:dev}
        group: ${NACOS_GROUP:DEV_GROUP}
        file-extension: yaml
        shared-configs:
          - data-id: atlas-common.yaml
            group: COMMON_GROUP
            refresh: true

  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://127.0.0.1:5432/atlas?currentSchema=atlas_system&useUnicode=true&characterEncoding=utf-8
    username: admin
    password: PK3uK7pUIwUTi1
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true

server:
  port: ${SERVER_PORT:8085}
```

### 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/atlas-system-1.0.0.jar
```

### 使用示例

#### 1. 查询用户信息

```bash
# 根据用户ID查询
curl http://localhost:8085/api/v1/users/1

# 根据用户名查询
curl http://localhost:8085/api/v1/users/by-username?username=admin
```

响应示例：

```json
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

```bash
# 查询用户角色列表
curl http://localhost:8085/api/v1/users/1/roles

# 查询用户权限列表
curl http://localhost:8085/api/v1/users/1/permissions

# 查询用户完整权限信息
curl http://localhost:8085/api/v1/users/1/authorities
```

响应示例：

```json
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "userId": 1,
    "roles": ["admin", "user"],
    "permissions": ["user:read", "user:write", "user:delete"]
  }
}
```

#### 3. 创建用户

```bash
curl -X POST http://localhost:8085/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "nickname": "新用户",
    "email": "newuser@example.com",
    "phone": "13900139000"
  }'
```

#### 4. 创建角色

```bash
curl -X POST http://localhost:8085/api/v1/roles \
  -H "Content-Type: application/json" \
  -d '{
    "roleCode": "editor",
    "roleName": "编辑",
    "description": "内容编辑角色"
  }'
```

#### 5. 创建权限

```bash
curl -X POST http://localhost:8085/api/v1/permissions \
  -H "Content-Type: application/json" \
  -d '{
    "permissionCode": "article:edit",
    "permissionName": "文章编辑",
    "description": "编辑文章权限"
  }'
```

#### 6. 为用户分配角色

```bash
curl -X POST http://localhost:8085/api/v1/users/1/roles \
  -H "Content-Type: application/json" \
  -d '{
    "roleId": 1
  }'
```

#### 7. 为角色分配权限

```bash
curl -X POST http://localhost:8085/api/v1/roles/1/permissions \
  -H "Content-Type: application/json" \
  -d '{
    "permissionId": 1
  }'
```

## API 接口

### 查询接口（实现 atlas-system-api）

1. **用户查询**
   - `GET /api/v1/users/{userId}` - 根据用户ID查询用户信息
   - `GET /api/v1/users/by-username?username={username}` - 根据用户名查询用户信息

2. **权限查询**
   - `GET /api/v1/users/{userId}/roles` - 查询用户角色列表
   - `GET /api/v1/users/{userId}/permissions` - 查询用户权限列表
   - `GET /api/v1/users/{userId}/authorities` - 查询用户完整权限信息（角色+权限）

### 管理接口

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

### 核心依赖

- `atlas-system-api`: API 接口定义（Feign 接口契约、DTO、枚举）
- `atlas-common-infra-db`: MyBatis-Plus 配置、数据库基础设施
- `atlas-common-infra-web`: 全局异常处理、参数校验返回
- `atlas-common-feature-core`: 统一响应格式、异常处理、错误码

### 技术依赖

- `spring-boot-starter-web`: Web 应用支持
- `spring-boot-starter-validation`: 参数校验
- `spring-security-crypto`: 密码加密（BCrypt）
- `spring-cloud-starter-openfeign`: Feign 客户端
- `mybatis-plus-boot-starter`: MyBatis-Plus 数据访问
- `postgresql`: PostgreSQL 数据库驱动
- `flyway-core`: 数据库迁移工具
- `spring-cloud-starter-alibaba-nacos-config`: Nacos 配置中心
- `spring-cloud-starter-alibaba-nacos-discovery`: Nacos 服务发现

## 数据库

### 表结构

- `sys_user`: 用户表
- `sys_role`: 角色表
- `sys_permission`: 权限表
- `sys_user_role`: 用户角色关联表
- `sys_role_permission`: 角色权限关联表

### 迁移脚本

- **Flyway 迁移脚本**: `src/main/resources/db/migration/`
  - `V1__Create_user_role_permission_tables.sql`: 创建用户、角色、权限相关表

- **SQL 脚本目录**: `sql/v1.0.0/`（按版本管理）
  - `001_create_user_table.sql`: 创建用户表
  - `002_create_role_table.sql`: 创建角色表
  - `003_create_permission_table.sql`: 创建权限表
  - `004_create_user_role_table.sql`: 创建用户角色关联表
  - `005_create_role_permission_table.sql`: 创建角色权限关联表
  - `README.md`: SQL 脚本说明文档

## 错误码

系统域错误码使用模块码 `03`，格式为 `MMTTSS`（6位数字）：

- **用户相关错误** (032001-032099): 用户不存在、用户名已存在等
- **角色相关错误** (032101-032199): 角色不存在、角色代码已存在等
- **权限相关错误** (032201-032299): 权限不存在、权限代码已存在等

详细错误码定义请参考：`com.atlas.system.constant.SystemErrorCode`

## 注意事项

1. **数据实时生效**: 数据保存到数据库后立即生效，无需重启服务或刷新缓存
2. **接口契约**: 查询接口必须严格按照 `atlas-system-api` 中定义的接口实现
3. **包结构**: 遵循业务模块按业务再按技术分层组织（`com.atlas.system.user.controller`）
4. **SQL 目录**: 在服务目录下建立 `sql` 目录，按版本管理 SQL 脚本
5. **密码加密**: 用户密码使用 BCrypt 算法加密存储
6. **全局异常处理**: 使用 `atlas-common-infra-web` 模块提供的全局异常处理器

## 测试

### 运行单元测试

```bash
mvn test
```

### 运行集成测试

```bash
mvn verify
```

### 测试覆盖率

单元测试覆盖率要求 ≥ 70%，主要覆盖：
- Service 层业务逻辑
- Controller 层接口功能
- Mapper 层数据访问（需要数据库连接）

## 相关文档

- [API 契约定义](../../specs/011-system-service/contracts/README.md)
- [数据模型定义](../../specs/011-system-service/data-model.md)
- [快速开始指南](../../specs/011-system-service/quickstart.md)
- [技术调研文档](../../specs/011-system-service/research.md)
- [实现任务清单](../../specs/011-system-service/tasks.md)

## Docker 部署

### 目录结构

```
atlas-service/atlas-system/
├── docker/
│   ├── Dockerfile.build    # 编译阶段 Dockerfile
│   └── Dockerfile.run      # 运行阶段 Dockerfile
├── src/
└── pom.xml
```

### 构建镜像

#### 方式一：本地构建后打包（推荐用于开发）

```bash
# 1. 本地 Maven 构建
mvn clean package -DskipTests

# 2. 构建运行镜像
docker build -f docker/Dockerfile.run -t atlas-system:latest .
```

#### 方式二：使用编译镜像构建

```bash
# 使用编译阶段 Dockerfile 构建（需要在项目根目录执行）
docker build -f atlas-service/atlas-system/docker/Dockerfile.build -t atlas-system-build .
```

### 运行容器

```bash
# 基本运行
docker run -d \
  --name atlas-system \
  -p 8085:8085 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e NACOS_SERVER_ADDR=host.docker.internal:8848 \
  atlas-system:latest

# 自定义 JVM 参数和数据库配置
docker run -d \
  --name atlas-system \
  -p 8085:8085 \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/atlas" \
  -e SPRING_DATASOURCE_USERNAME="admin" \
  -e SPRING_DATASOURCE_PASSWORD="password" \
  atlas-system:latest
```

### 健康检查

```bash
# 检查服务健康状态
curl http://localhost:8085/actuator/health

# 查看容器健康状态
docker inspect --format='{{.State.Health.Status}}' atlas-system
```

### 查看日志

```bash
docker logs -f atlas-system
```

## 开发指南

1. 遵循项目宪法和工程规范
2. 代码注释使用中文
3. 提交前运行 `mvn clean install` 确保构建通过
4. 代码审查时检查规范遵循情况
5. 使用 Spotless 格式化代码：`mvn spotless:apply`