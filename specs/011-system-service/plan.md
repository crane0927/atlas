# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **API 设计**: 遵循 RESTful 风格
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 识别并复用现有公共方法
- ✅ **模块化**: 遵循分层架构原则
- ✅ **包结构规范**: 业务模块按业务再按技术分层组织（`com.atlas.system.user.controller`）
- ✅ **数据库技术**: 使用 PostgreSQL + MyBatis-Plus + Flyway/Liquibase
- ✅ **SQL 目录管理**: 在服务目录下建立 `sql` 目录，按版本管理 SQL 脚本

**宪法合规性评估**:
- ✅ 所有技术栈版本符合宪法要求
- ✅ 模块归属 `atlas-service/atlas-system`，符合模块化设计原则
- ✅ 实现 `atlas-system-api` 中定义的接口，符合接口契约规范
- ✅ 使用 PostgreSQL + MyBatis-Plus，符合数据库技术选型要求
- ✅ 使用 Flyway 或 Liquibase 管理数据库迁移脚本，符合宪法要求
- ✅ 在服务目录下建立 `sql` 目录，按版本管理 SQL 脚本，符合 SQL 目录管理规范
- ✅ 包结构按业务再按技术分层组织，符合业务模块规范
- ✅ 所有代码将使用中文注释
- ✅ 复用 `atlas-common-infra-db` 模块的 MyBatis-Plus 配置
- ✅ 复用 `atlas-common-feature-core` 模块的 Result、错误码等

## 功能概述

实现 `atlas-system` 服务，提供用户、角色、权限管理的最小闭环功能，实现 `atlas-system-api` 中定义的接口契约，支持 `atlas-auth` 服务的用户认证和权限授权需求。

**核心价值**:
1. **服务解耦**: System 服务独立管理用户、角色、权限数据，Auth 服务通过 Feign 接口调用，实现服务解耦
2. **权限管理**: 提供完整的 RBAC（基于角色的访问控制）模型，支持用户-角色-权限的关联管理
3. **实时生效**: 数据变更后立即生效，无需重启服务或手动刷新缓存
4. **标准化**: 遵循项目宪法规范，使用统一的技术栈和代码规范

**业务目标**:
- Auth 服务能够成功查询用户信息进行认证
- Auth 服务能够成功查询用户权限进行授权
- Gateway 能够通过 Auth 服务验证权限，正确放行或拒绝请求
- 新增用户/角色/权限后，授权立即生效

## 技术方案

### 架构设计

**模块结构**:
```
atlas/
└── atlas-service/             # 服务模块
    └── atlas-system/          # 系统服务模块
        ├── pom.xml            # 模块 POM
        ├── README.md          # 模块文档
        ├── sql/               # SQL 脚本目录（按版本管理）
        │   └── v1.0.0/        # 版本 1.0.0 的 SQL 脚本
        │       ├── 001_create_user_table.sql
        │       ├── 002_create_role_table.sql
        │       ├── 003_create_permission_table.sql
        │       ├── 004_create_user_role_table.sql
        │       ├── 005_create_role_permission_table.sql
        │       └── README.md
        └── src/
            ├── main/
            │   ├── java/
            │   │   └── com/atlas/system/
            │   │       ├── SystemApplication.java        # 启动类
            │   │       ├── user/                         # 用户业务模块
            │   │       │   ├── controller/               # Controller 层
            │   │       │   │   ├── UserController.java   # 用户查询接口（实现 UserQueryApi）
            │   │       │   │   └── UserManagementController.java  # 用户管理接口
            │   │       │   ├── service/                  # Service 层
            │   │       │   │   ├── UserService.java
            │   │       │   │   └── impl/
            │   │       │   │       └── UserServiceImpl.java
            │   │       │   ├── mapper/                   # Mapper 层
            │   │       │   │   ├── UserMapper.java
            │   │       │   │   └── UserRoleMapper.java   # 用户角色关联 Mapper
            │   │       │   └── model/                    # 模型层
            │   │       │       ├── entity/               # 实体类
            │   │       │       │   ├── User.java
            │   │       │       │   └── UserRole.java    # 用户角色关联实体
            │   │       │       ├── dto/                  # DTO（如需要，用于管理接口）
            │   │       │       │   └── UserCreateDTO.java
            │   │       │       └── vo/                   # VO（如需要，用于管理接口）
            │   │       │           └── UserVO.java
            │   │       ├── role/                         # 角色业务模块
            │   │       │   ├── controller/               # Controller 层
            │   │       │   │   └── RoleManagementController.java
            │   │       │   ├── service/                  # Service 层
            │   │       │   │   ├── RoleService.java
            │   │       │   │   └── impl/
            │   │       │   │       └── RoleServiceImpl.java
            │   │       │   ├── mapper/                   # Mapper 层
            │   │       │   │   ├── RoleMapper.java
            │   │       │   │   └── RolePermissionMapper.java  # 角色权限关联 Mapper
            │   │       │   └── model/                    # 模型层
            │   │       │       ├── entity/               # 实体类
            │   │       │       │   ├── Role.java
            │   │       │       │   └── RolePermission.java  # 角色权限关联实体
            │   │       │       ├── dto/                  # DTO
            │   │       │       │   └── RoleCreateDTO.java
            │   │       │       └── vo/                   # VO
            │   │       │           └── RoleVO.java
            │   │       ├── permission/                  # 权限业务模块
            │   │       │   ├── controller/               # Controller 层
            │   │       │   │   ├── PermissionController.java  # 权限查询接口（实现 PermissionQueryApi）
            │   │       │   │   └── PermissionManagementController.java  # 权限管理接口
            │   │       │   ├── service/                  # Service 层
            │   │       │   │   ├── PermissionService.java
            │   │       │   │   └── impl/
            │   │       │   │       └── PermissionServiceImpl.java
            │   │       │   ├── mapper/                   # Mapper 层
            │   │       │   │   └── PermissionMapper.java
            │   │       │   └── model/                    # 模型层
            │   │       │       ├── entity/               # 实体类
            │   │       │       │   └── Permission.java
            │   │       │       ├── dto/                  # DTO
            │   │       │       │   └── PermissionCreateDTO.java
            │   │       │       └── vo/                   # VO
            │   │       │           └── PermissionVO.java
            │   │       ├── constant/                     # 常量类
            │   │       │   └── SystemErrorCode.java      # 系统域错误码
            │   │       └── config/                       # 配置类
            │   │           ├── MyBatisPlusConfig.java    # MyBatis-Plus 配置（可选，复用 common-infra-db）
            │   │           └── FeignConfig.java         # Feign 配置（可选）
            │   └── resources/
            │       ├── application.yml                   # 应用配置
            │       ├── application-dev.yml               # 开发环境配置
            │       ├── application-prod.yml              # 生产环境配置
            │       ├── db/migration/                    # Flyway 迁移脚本目录
            │       │   └── V1__Create_user_role_permission_tables.sql
            │       └── mapper/                          # MyBatis XML 映射文件（如需要）
            │           └── user/
            │               └── UserMapper.xml
            └── test/
                ├── java/
                │   └── com/atlas/system/
                │       ├── user/
                │       │   ├── controller/
                │       │   ├── service/
                │       │   └── mapper/
                │       ├── role/
                │       │   ├── controller/
                │       │   ├── service/
                │       │   └── mapper/
                │       └── permission/
                │           ├── controller/
                │           ├── service/
                │           └── mapper/
                └── resources/
                    └── application-test.yml             # 测试环境配置
```

**核心组件**:

1. **Controller 层**: 实现 `atlas-system-api` 中定义的 Feign 接口
   - `UserController`: 实现 `UserQueryApi` 接口
   - `PermissionController`: 实现 `PermissionQueryApi` 接口
   - 用户/角色/权限管理 Controller（用于创建和关联）

2. **Service 层**: 实现业务逻辑
   - `UserService`: 用户查询业务逻辑
   - `PermissionService`: 权限查询业务逻辑
   - `RoleService`: 角色管理业务逻辑（可选）
   - `PermissionService`: 权限管理业务逻辑（可选）

3. **Mapper 层**: 数据访问层
   - `UserMapper`: 用户数据访问
   - `RoleMapper`: 角色数据访问
   - `PermissionMapper`: 权限数据访问
   - `UserRoleMapper`: 用户角色关联数据访问
   - `RolePermissionMapper`: 角色权限关联数据访问

4. **Entity 层**: 实体类
   - `User`: 用户实体
   - `Role`: 角色实体
   - `Permission`: 权限实体
   - `UserRole`: 用户角色关联实体
   - `RolePermission`: 角色权限关联实体

### 技术选型

**构建工具**: Maven 3.8+
- **理由**: 项目使用 Maven，继承父 POM 配置

**核心依赖**:
- **spring-boot-starter-web**: Spring Boot Web 支持（版本：4.0.1，由父 POM 管理）
- **spring-cloud-starter-alibaba-nacos-discovery**: Nacos 服务注册与发现（版本：2025.1.0，由父 POM 管理）
- **spring-cloud-starter-alibaba-nacos-config**: Nacos 配置管理（版本：2025.1.0，由父 POM 管理）
- **spring-cloud-starter-openfeign**: OpenFeign 服务调用（版本：2025.1.0，由父 POM 管理）
- **mybatis-plus-boot-starter**: MyBatis-Plus 核心依赖（版本：3.5.8，由父 POM 管理）
- **postgresql**: PostgreSQL 驱动（版本：42.7.4，由父 POM 管理）
- **flyway-core** 或 **liquibase-core**: 数据库迁移工具（版本：由父 POM 管理）
- **atlas-system-api**: System API 接口定义（版本：1.0.0）
- **atlas-common-infra-db**: 数据库基础设施（版本：1.0.0）
- **atlas-common-feature-core**: 核心工具类（版本：1.0.0）

**技术决策**:

1. **数据库迁移工具**: 选择 Flyway 或 Liquibase
   - **决策**: 需要研究两种工具的优缺点，选择更适合项目的工具
   - **理由**: 两种工具都符合宪法要求，需要根据项目实际情况选择
   - **研究任务**: 比较 Flyway 和 Liquibase 的功能、易用性、社区支持等

2. **数据访问方式**: 使用 MyBatis-Plus
   - **理由**: 符合宪法要求，复用 `atlas-common-infra-db` 模块的配置
   - **实现方式**: 继承 `BaseMapper<T>`，使用 MyBatis-Plus 提供的 CRUD 方法

3. **实体类设计**: 继承 `BaseEntity`（可选）
   - **理由**: `atlas-common-infra-db` 模块提供了 `BaseEntity`，包含审计字段和逻辑删除字段
   - **决策**: 根据实际需求决定是否继承 `BaseEntity`

4. **DTO 转换**: 使用 MapStruct 或手动转换
   - **决策**: 需要研究 MapStruct 的使用方式，或使用手动转换
   - **理由**: Entity 到 DTO 的转换是必需的，需要选择合适的方式

5. **服务注册**: 使用 Nacos
   - **理由**: 符合宪法要求，使用 Spring Cloud Alibaba 的 Nacos

6. **接口实现**: 实现 `atlas-system-api` 中定义的 Feign 接口
   - **理由**: Controller 需要实现 API 模块中定义的接口，确保接口契约一致性

## 实施计划

### 阶段 0: 研究与技术选型

**目标**: 解决技术选型问题，完成技术调研

**任务**:
1. 研究 Flyway 和 Liquibase 的优缺点，选择数据库迁移工具
2. 研究 MyBatis-Plus 在 Spring Boot 4.0.1 中的最佳实践
3. 研究 MapStruct 的使用方式，决定 DTO 转换方案
4. 研究 PostgreSQL 在 Spring Boot 中的配置方式
5. 研究 Nacos 服务注册与发现的配置方式

**输出**: `research.md`

**验收标准**:
- ✅ 所有技术选型问题已解决
- ✅ 研究文档完整，包含决策和理由

### 阶段 1: 项目初始化和数据库设计

**目标**: 创建模块结构，设计数据库表结构，创建迁移脚本

**任务**:
1. 创建 `atlas-service/atlas-system` 模块目录结构
2. 创建 `pom.xml`，配置依赖
3. 设计数据库表结构（user、role、permission、user_role、role_permission）
4. 创建 Flyway/Liquibase 迁移脚本
5. 在服务目录下建立 `sql` 目录，按版本管理 SQL 脚本
6. 创建 `README.md` 文档
7. 创建 `application.yml` 配置文件

**输出**: 
- 模块结构
- 数据库迁移脚本
- `sql/v1.0.0/` 目录及 SQL 脚本
- `data-model.md`

**验收标准**:
- ✅ 模块结构创建完成
- ✅ 依赖配置正确
- ✅ 数据库表结构设计合理
- ✅ 迁移脚本创建成功
- ✅ `sql` 目录结构符合规范

### 阶段 2: 实体类和 Mapper 实现

**目标**: 实现所有实体类和 Mapper 接口

**任务**:
1. 创建 `User` 实体类
2. 创建 `Role` 实体类
3. 创建 `Permission` 实体类
4. 创建 `UserRole` 实体类
5. 创建 `RolePermission` 实体类
6. 创建对应的 Mapper 接口
7. 实现必要的自定义查询方法

**输出**: 
- 所有实体类
- 所有 Mapper 接口

**验收标准**:
- ✅ 实体类定义完整，字段与数据库表对应
- ✅ Mapper 接口定义完整，包含必要的查询方法
- ✅ 所有类和方法包含完整的中文注释

### 阶段 3: Service 层实现

**目标**: 实现 Service 层业务逻辑

**任务**:
1. 创建 `UserService` 接口和实现类
2. 实现根据用户ID查询用户信息的方法
3. 实现根据用户名查询用户信息的方法
4. 实现 Entity 到 DTO 的转换
5. 创建 `PermissionService` 接口和实现类
6. 实现查询用户角色列表的方法
7. 实现查询用户权限列表的方法
8. 实现查询用户完整权限信息的方法

**输出**: 
- Service 接口和实现类
- DTO 转换逻辑

**验收标准**:
- ✅ Service 接口和实现类定义完整
- ✅ 能够正确查询用户信息和权限
- ✅ Entity 到 DTO 转换正确
- ✅ 所有方法包含完整的中文注释

### 阶段 4: Controller 层实现

**目标**: 实现 Controller 层，提供 RESTful API 接口

**任务**:
1. 创建 `UserController`，实现 `UserQueryApi` 接口
2. 实现 `GET /api/v1/users/{userId}` 接口
3. 实现 `GET /api/v1/users/by-username?username={username}` 接口
4. 创建 `PermissionController`，实现 `PermissionQueryApi` 接口
5. 实现 `GET /api/v1/users/{userId}/roles` 接口
6. 实现 `GET /api/v1/users/{userId}/permissions` 接口
7. 实现 `GET /api/v1/users/{userId}/authorities` 接口
8. 配置统一的异常处理

**输出**: 
- Controller 类
- API 接口实现

**验收标准**:
- ✅ Controller 定义完整，实现所有必需的接口
- ✅ 接口路径符合 RESTful 规范
- ✅ 接口返回统一的数据格式
- ✅ 异常处理正确
- ✅ 所有方法包含完整的中文注释

### 阶段 5: 用户/角色/权限管理功能实现

**目标**: 实现用户、角色、权限的创建和关联功能

**任务**:
1. 创建用户管理 Controller，提供创建用户的接口
2. 创建角色管理 Controller，提供创建角色的接口
3. 创建权限管理 Controller，提供创建权限的接口
4. 提供用户角色关联的接口（为用户分配角色）
5. 提供角色权限关联的接口（为角色分配权限）
6. 实现相应的 Service 和 Mapper 方法

**输出**: 
- 用户/角色/权限管理 Controller
- 相应的 Service 和 Mapper 方法

**验收标准**:
- ✅ 能够创建用户、角色、权限
- ✅ 能够建立用户与角色的关联
- ✅ 能够建立角色与权限的关联
- ✅ 数据保存后立即生效

### 阶段 6: 测试和文档

**目标**: 完成单元测试、集成测试和文档编写

**任务**:
1. 编写 Service 层单元测试（覆盖率 ≥ 70%）
2. 编写 Controller 层集成测试
3. 编写 API 测试
4. 编写验收测试
5. 更新 `README.md` 文档
6. 创建 `quickstart.md` 快速开始指南

**输出**: 
- 测试代码
- 文档

**验收标准**:
- ✅ 单元测试覆盖率 ≥ 70%
- ✅ 集成测试覆盖主要业务流程
- ✅ API 测试覆盖所有接口
- ✅ 验收测试通过
- ✅ 文档完整

## 风险评估

### 技术风险

1. **数据库迁移工具选择**
   - **风险**: Flyway 和 Liquibase 各有优缺点，选择不当可能影响后续维护
   - **影响**: 中等
   - **应对**: 在阶段 0 进行充分调研，选择更适合项目的工具

2. **MyBatis-Plus 版本兼容性**
   - **风险**: MyBatis-Plus 3.5.8 与 Spring Boot 4.0.1 可能存在兼容性问题
   - **影响**: 低
   - **应对**: 使用 `atlas-common-infra-db` 模块的统一配置，已验证兼容性

3. **DTO 转换性能**
   - **风险**: 手动转换 DTO 可能影响性能，MapStruct 需要额外配置
   - **影响**: 低
   - **应对**: 在阶段 0 研究 MapStruct 的使用方式，选择合适方案

### 业务风险

1. **数据一致性**
   - **风险**: 用户、角色、权限的关联关系复杂，可能出现数据不一致
   - **影响**: 高
   - **应对**: 使用数据库事务确保数据一致性，编写完善的测试用例

2. **性能问题**
   - **风险**: 权限查询涉及多表关联，可能影响性能
   - **影响**: 中等
   - **应对**: 优化 SQL 查询，必要时添加索引，考虑使用缓存（但需确保数据实时性）

3. **接口兼容性**
   - **风险**: 实现 `atlas-system-api` 接口时可能不符合接口契约
   - **影响**: 高
   - **应对**: 严格按照 API 模块定义的接口实现，编写接口测试验证

### 集成风险

1. **服务注册失败**
   - **风险**: System 服务无法注册到 Nacos，Auth 服务无法调用
   - **影响**: 高
   - **应对**: 检查 Nacos 配置，确保服务能够正常注册

2. **Feign 调用失败**
   - **风险**: Auth 服务无法通过 Feign 调用 System 服务
   - **影响**: 高
   - **应对**: 检查 Feign 配置，确保接口路径和参数正确

3. **数据实时性**
   - **风险**: 新增用户/角色/权限后，Auth 服务查询不到最新数据
   - **影响**: 高
   - **应对**: 确保不使用缓存，每次查询都从数据库获取最新数据

## 验收标准

### 功能验收

1. **服务启动**: System 服务能够成功启动并注册到 Nacos
2. **接口实现**: 所有 `atlas-system-api` 中定义的接口都已实现
3. **数据管理**: 能够创建用户、角色、权限及关联关系
4. **查询功能**: 能够正确查询用户信息和权限信息

### 集成验收

1. **Auth 集成**: Auth 服务能够成功调用 System 服务查询用户和权限
2. **Gateway 集成**: Gateway 能够通过 Auth 服务验证权限，正确放行或拒绝请求
3. **实时生效**: 新增用户/角色/权限后，授权立即生效，无需重启服务

### 技术验收

1. **数据库**: 使用 PostgreSQL 数据库，表结构设计合理
2. **数据访问**: 使用 MyBatis-Plus 进行数据访问，代码规范
3. **迁移脚本**: 使用 Flyway 或 Liquibase 管理数据库迁移脚本
4. **SQL 目录**: 在服务目录下建立 `sql` 目录，按版本管理 SQL 脚本
5. **包结构**: 包结构符合业务模块规范（按业务再按技术分层组织）
6. **代码质量**: 所有代码遵循项目规范，包含完整的中文注释
7. **测试覆盖**: 单元测试和集成测试覆盖主要功能，测试通过率 100%
