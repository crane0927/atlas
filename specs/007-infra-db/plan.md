# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 识别并复用现有公共方法和工具类
- ✅ **模块化**: 遵循分层架构原则
- ✅ **包名规范**: 遵循 `com.atlas.common.infra.db` 包名结构
- ✅ **配置文件格式**: 使用 YAML 格式
- ✅ **数据库技术选型**: 使用 PostgreSQL 作为主要关系型数据库，使用 MyBatis-Plus 进行数据访问

**宪法合规性评估**:
- ✅ 所有技术栈版本符合宪法要求
- ✅ 模块归属 `atlas-common-infra-db`，符合模块化设计原则
- ✅ 提供可复用的数据库访问基础设施组件，符合代码复用原则
- ✅ 所有代码将使用中文注释
- ✅ 遵循包名规范
- ✅ 使用 MyBatis-Plus 进行数据访问，符合宪法要求
- ✅ 复用 `atlas-common-feature-security` 模块获取当前用户信息（可选，用于审计字段填充）

## 功能概述

实现 `atlas-common-infra-db` 模块，为 Atlas 项目提供统一的数据库访问基础设施，包括 MyBatis-Plus 基础配置、分页插件、审计字段填充等功能。该模块为所有业务模块提供统一的数据库访问规范，确保数据访问的一致性、可维护性和可追溯性。

该模块确保：

1. **统一性**: 所有模块使用统一的 MyBatis-Plus 配置，配置一致性达到 100%
2. **易用性**: 开发人员可以在 5 分钟内理解并使用 MyBatis-Plus 功能，无需深入了解底层实现
3. **规范性**: 所有分页查询使用统一的分页插件，分页查询规范性达到 100%
4. **可追溯性**: 如果实现审计字段填充，所有数据操作都有完整的审计信息
5. **可维护性**: 配置集中管理，新增配置项处理时间不超过 5 分钟

## 技术方案

### 架构设计

**模块结构**:
```
atlas/
└── atlas-common/              # 公共模块
    └── atlas-common-infra/    # 基础设施模块
        └── atlas-common-infra-db/  # 数据库基础设施模块
            ├── pom.xml                    # 模块 POM
            ├── README.md                  # 模块文档
            ├── src/
            │   ├── main/java/
            │   │   └── com/atlas/common/infra/db/
            │   │       ├── config/        # MyBatis-Plus 配置
            │   │       │   ├── MyBatisPlusConfig.java
            │   │       │   └── MyBatisPlusProperties.java
            │   │       ├── handler/        # 审计字段填充处理器
            │   │       │   └── AuditMetaObjectHandler.java
            │   │       └── entity/         # 基础实体类（可选）
            │   │           └── BaseEntity.java
            │   └── test/java/             # 测试代码
            │       └── com/atlas/common/infra/db/
            │           ├── config/
            │           └── handler/
```

**核心组件**:

1. **MyBatis-Plus 配置**: `MyBatisPlusConfig`
   - 使用 `@Configuration` 注解
   - 配置 `MybatisPlusInterceptor` 拦截器
   - 配置全局设置（字段策略、逻辑删除等）
   - 支持通过配置文件自定义配置参数

2. **分页插件**: `PaginationInnerInterceptor`
   - 添加到 `MybatisPlusInterceptor` 中
   - 支持自定义分页参数
   - 自动拦截带有 `Page` 参数的方法

3. **审计字段填充处理器**: `AuditMetaObjectHandler`
   - 实现 `MetaObjectHandler` 接口
   - 处理创建时间、更新时间、创建人、更新人等字段的自动填充
   - 从安全上下文获取当前用户信息（可选）

### 技术选型

**构建工具**: Maven 3.8+
- **理由**: 项目使用 Maven，继承父 POM 配置

**核心依赖**:
- **mybatis-plus-boot-starter**: MyBatis-Plus 核心依赖（版本：3.5.8，由父 POM 管理）
- **postgresql**: PostgreSQL 驱动（版本：42.7.4，由父 POM 管理）
- **atlas-common-feature-security**: 安全模块（用于获取当前用户信息，可选）

**技术决策**:

1. **MyBatis-Plus 版本**: 使用 3.5.8 版本（由父 POM 管理）
   - **理由**: 3.5.8 版本稳定，与 Spring Boot 4.0.1 兼容
   - **注意**: MyBatis-Plus 3.5.x 使用 `MybatisPlusInterceptor` 而不是旧的 `PaginationInterceptor`

2. **分页插件**: 使用 `PaginationInnerInterceptor`
   - **理由**: MyBatis-Plus 3.5.x 推荐使用 `PaginationInnerInterceptor`，功能更强大
   - **格式**: 添加到 `MybatisPlusInterceptor` 中，支持多种数据库类型

3. **审计字段填充**: 使用 `MetaObjectHandler` 接口
   - **理由**: MyBatis-Plus 提供的标准接口，易于实现和维护
   - **实现方式**: 实现 `insertFill()` 和 `updateFill()` 方法

4. **数据库类型**: PostgreSQL
   - **理由**: 符合项目宪法要求，使用 PostgreSQL 作为主要关系型数据库

## 实施计划

### 阶段 1: 项目初始化和基础配置

**目标**: 创建模块结构，配置依赖，搭建基础框架

**任务**:
1. 创建 `atlas-common-infra-db` 模块目录结构
2. 创建 `pom.xml`，配置依赖（mybatis-plus-boot-starter、postgresql、atlas-common-feature-security）
3. 创建包结构（config、handler、entity）
4. 创建 `README.md` 文档

**验收标准**:
- 模块结构创建完成
- 依赖配置正确
- 包结构符合规范

### 阶段 2: MyBatis-Plus 基础配置实现

**目标**: 实现统一的 MyBatis-Plus 配置

**任务**:
1. 创建 `MyBatisPlusProperties` 配置属性类
2. 创建 `MyBatisPlusConfig` 配置类
3. 配置 `MybatisPlusInterceptor` 拦截器
4. 配置全局设置（字段策略、逻辑删除等，可选）
5. 支持通过配置文件自定义配置参数
6. 编写单元测试

**验收标准**:
- MyBatis-Plus 配置类可以正确创建
- 配置类自动注册到 Spring 容器
- 配置参数可以通过配置文件自定义
- 业务模块引入该模块后即可使用 MyBatis-Plus 功能

### 阶段 3: 分页插件配置实现

**目标**: 实现统一的分页插件配置

**任务**:
1. 在 `MyBatisPlusConfig` 中添加 `PaginationInnerInterceptor` 分页插件
2. 配置分页参数（页码参数名、每页数量参数名、最大每页数量等）
3. 设置数据库类型（PostgreSQL）
4. 支持通过配置文件自定义分页参数
5. 编写单元测试

**验收标准**:
- 分页插件可以正确配置
- 分页查询可以正确执行
- 分页结果包含完整的分页信息
- 分页参数可以通过配置文件自定义

### 阶段 4: 审计字段填充实现（可后置）

**目标**: 实现审计字段自动填充功能

**任务**:
1. 创建 `AuditMetaObjectHandler` 处理器类
2. 实现 `insertFill()` 方法，填充创建时间和创建人
3. 实现 `updateFill()` 方法，填充更新时间和更新人
4. 集成安全模块，从安全上下文获取当前用户信息（可选）
5. 创建 `BaseEntity` 基础实体类（可选，提供审计字段定义）
6. 编写单元测试

**验收标准**:
- 插入数据时自动填充创建时间和创建人
- 更新数据时自动填充更新时间和更新人
- 审计字段填充功能可以正确工作
- 审计字段填充功能可以后置实现（不影响核心功能）

### 阶段 5: 文档和测试完善

**目标**: 完善文档，提供使用示例，确保测试覆盖率

**任务**:
1. 更新 `README.md`，添加使用示例
2. 创建 `quickstart.md` 快速开始指南
3. 运行 Spotless 格式化检查
4. 运行 Maven Enforcer 检查
5. 验证单元测试覆盖率 ≥ 80%

**验收标准**:
- 文档完整清晰
- 使用示例可以正常运行
- 单元测试覆盖率 ≥ 80%

## 风险评估

### 风险 1: MyBatis-Plus 版本兼容性

**描述**: MyBatis-Plus 3.5.8 版本可能与 Spring Boot 4.0.1 存在兼容性问题

**影响**: 配置类无法正确创建或功能异常

**应对措施**:
- 使用父 POM 管理的版本，确保版本一致性
- 在实施前进行版本兼容性测试
- 如有问题，考虑降级或升级版本

### 风险 2: 分页插件配置错误

**描述**: 分页插件配置错误导致分页查询失败

**影响**: 分页功能无法使用

**应对措施**:
- 参考 MyBatis-Plus 官方文档进行配置
- 编写完整的单元测试验证分页功能
- 提供详细的配置示例和使用文档

### 风险 3: 审计字段填充依赖安全模块

**描述**: 审计字段填充功能依赖 `atlas-common-feature-security` 模块获取当前用户信息

**影响**: 如果安全模块未实现，审计字段填充功能无法获取用户信息

**应对措施**:
- 审计字段填充功能标记为可后置实现
- 如果安全模块未实现，创建人和更新人字段可以填充默认值或留空
- 提供配置选项，允许禁用用户信息填充

### 风险 4: PostgreSQL 数据库连接配置

**描述**: 数据库连接配置错误导致无法连接数据库

**影响**: 数据库操作失败

**应对措施**:
- 数据库连接配置由 Spring Boot 自动配置，本模块不涉及连接配置
- 提供配置示例文档
- 在文档中说明数据库连接配置的位置

## 验收标准

### 定量指标

- **配置正确性**: MyBatis-Plus 配置类可以正确创建并注册到 Spring 容器（100%）
- **分页功能**: 分页查询可以正确执行，分页结果包含完整信息（100%）
- **审计字段填充**: 如果实现，审计字段可以正确自动填充（100%）
- **测试覆盖率**: 单元测试覆盖率 ≥ 80%

### 定性指标

- **易用性**: 业务模块引入该模块后即可使用 MyBatis-Plus 功能，无需额外配置
- **一致性**: 所有业务模块使用统一的数据库访问规范
- **可维护性**: 配置集中管理，便于维护和扩展
- **可扩展性**: 支持通过配置文件自定义配置参数
