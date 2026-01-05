<!--
Sync Impact Report:
Version: 0.3.6 → 0.3.7
创建日期: 2025-01-27
最后修订: 2026-01-05
修改的原则: 
  - 原则 11: 模块化设计 - 新增模块文档要求（每个模块下需要有 README.md）
新增章节: 无
移除章节: 无
模板更新状态:
  - .specify/templates/plan-template.md: ✅ 已同步
  - .specify/templates/spec-template.md: ✅ 已同步
  - .specify/templates/tasks-template.md: ✅ 已同步
  - .specify/templates/commands/*.md: ✅ 已同步
后续待办: 无
-->

# 项目宪法

**项目名称**: Atlas  
**版本**: 0.3.7  
**批准日期**: 2025-01-27  
**最后修订日期**: 2026-01-05

## 概述

本宪法定义了 Atlas 项目的核心原则、技术标准和治理规则。Atlas 是一个基于 Spring Boot 和 Spring Cloud 的企业级快速开发框架，旨在提供高效、规范、可维护的微服务开发解决方案。

## 技术栈原则

### 原则 1: Java 版本要求

**规则**: 项目必须使用 JDK 21 作为最低和唯一支持的 Java 版本。

**理由**: JDK 21 是长期支持（LTS）版本，提供了现代 Java 特性（如虚拟线程、模式匹配、记录类等），能够提升开发效率和运行时性能。所有代码必须兼容 JDK 21，不得使用已废弃的 API 或特性。

**验证**: 
- `pom.xml` 或 `build.gradle` 中必须明确指定 Java 版本为 21
- CI/CD 流水线必须使用 JDK 21 进行构建和测试
- 代码审查时检查是否使用了 JDK 21 不支持的特性

### 原则 2: Spring Boot 版本锁定

**规则**: 项目必须使用 Spring Boot 4.0.1 版本，不得随意升级或降级。

**理由**: Spring Boot 4.0.1 与 JDK 21 完全兼容，提供了最新的框架特性和安全补丁。版本锁定确保团队使用统一的技术栈，避免因版本差异导致的兼容性问题。

**验证**:
- `pom.xml` 中的 `spring-boot-starter-parent` 版本必须为 4.0.1
- 所有 Spring Boot 相关依赖的版本必须由父 POM 管理，不得显式指定版本号

### 原则 3: Spring Cloud 生态版本

**规则**: 项目必须使用 Spring Cloud 2025.1.0 和 Spring Cloud Alibaba 2025.1.0 版本。

**理由**: 这两个版本与 Spring Boot 4.0.1 完全兼容，提供了完整的微服务解决方案，包括服务注册与发现、配置管理、网关、熔断降级等功能。版本统一确保微服务组件之间的兼容性。

**验证**:
- `pom.xml` 的 `dependencyManagement` 中必须包含这两个 BOM
- 所有 Spring Cloud 和 Spring Cloud Alibaba 组件的版本必须由 BOM 管理

### 原则 4: 数据库技术选型

**规则**: 项目必须使用 PostgreSQL 作为主要关系型数据库。

**理由**: PostgreSQL 是功能强大的开源关系型数据库，具有优秀的性能、可靠性和扩展性。支持 ACID 事务、复杂查询、JSON 数据类型、全文搜索等高级特性，适合企业级应用场景。

**具体要求**:
- 所有持久化数据必须存储在 PostgreSQL 数据库中
- 数据库连接配置必须使用 MyBatis-Plus
- 数据库迁移使用 Flyway 或 Liquibase 进行版本管理
- 禁止使用其他关系型数据库（如 MySQL、Oracle）作为主数据库
- 如需使用其他数据库（如 Redis、MongoDB），仅作为缓存或特定场景的辅助存储
- 不需要数据库连接的模块（如网关、配置中心等）无需配置数据源

**验证**:
- 需要数据库连接的模块必须使用 MyBatis-Plus 进行数据访问
- 数据库迁移脚本必须使用 Flyway 或 Liquibase 管理
- 代码审查时检查是否有直接使用其他关系型数据库的情况
- 代码审查时检查是否使用了 Spring Data JPA 或其他 ORM 框架

### 原则 5: 组件优先使用 Spring Cloud 生态

**规则**: 所有功能组件必须优先使用 Spring Cloud 和 Spring Cloud Alibaba 提供的官方组件，避免引入第三方替代方案。

**具体要求**:
- **服务注册与发现**: 优先使用 Nacos（Spring Cloud Alibaba），避免使用 Eureka、Consul 等
- **配置管理**: 优先使用 Nacos Config，避免使用 Spring Cloud Config Server
- **API 网关**: 优先使用 Spring Cloud Gateway，避免使用 Zuul、Kong 等
- **服务调用**: 优先使用 OpenFeign，避免使用 RestTemplate、OkHttp 等原生 HTTP 客户端
- **熔断降级**: 优先使用 Sentinel（Spring Cloud Alibaba），避免使用 Hystrix
- **分布式事务**: 优先使用 Seata（Spring Cloud Alibaba），避免使用其他分布式事务方案
- **消息队列**: 优先使用 RocketMQ（Spring Cloud Alibaba），避免使用 RabbitMQ、Kafka 等
- **链路追踪**: 优先使用 SkyWalking（企业常用方案，推荐）或 Spring 官方 Micrometer Tracing 生态；避免 Zipkin/Jaeger 等非统一方案
- **限流**: 优先使用 Sentinel，避免使用 Guava RateLimiter 等
- **负载均衡**: 使用 Spring Cloud LoadBalancer（Spring Cloud 官方组件）

**理由**: 
- Spring Cloud 和 Spring Cloud Alibaba 组件与 Spring Boot 深度集成，配置简单，维护成本低
- 官方组件经过充分测试，稳定性和兼容性有保障
- 统一的组件生态降低学习成本，便于团队协作
- 避免组件冲突和版本兼容性问题

**例外情况**:
- 如果 Spring Cloud 生态中没有对应组件，可以引入第三方组件，但必须在代码注释和文档中说明原因
- 特殊业务场景需要特定组件时，需要团队评审批准

**验证**:
- 代码审查时检查依赖引入，确认优先使用 Spring Cloud 生态组件
- `pom.xml` 中不应包含已被 Spring Cloud 生态替代的第三方组件（如 Hystrix、Eureka）
- 引入非 Spring Cloud 生态组件时，必须提供充分的理由说明

## 架构与设计原则

### 原则 6: RESTful API 设计

**规则**: 所有 HTTP 接口必须严格遵循 RESTful 设计风格。

**具体要求**:
- 使用标准 HTTP 方法（GET、POST、PUT、DELETE、PATCH）
- URL 路径使用名词复数形式，避免动词（如 `/api/users` 而非 `/api/getUsers`）
- 使用 HTTP 状态码表示操作结果（200、201、204、400、401、403、404、500 等）
- 响应体使用 JSON 格式，统一使用 `Result<T>` 包装
- 分页查询使用 `page` 和 `size` 参数，排序使用 `sort` 参数
- 版本控制通过 URL 路径实现（如 `/api/v1/users`）

**理由**: RESTful 风格是业界标准，提供了一致的 API 设计规范，便于前端调用和第三方集成，降低学习成本。

**验证**:
- 代码审查时检查 Controller 层的 URL 设计和 HTTP 方法使用
- API 文档必须明确标注每个接口的 HTTP 方法和状态码
- 禁止在 URL 中使用动词（如 `/createUser`、`/deleteUser`）

### 原则 7: 代码注释规范

**规则**: 代码注释优先使用中文，类、方法、复杂逻辑必须添加中文注释。

**具体要求**:
- 所有公共类必须使用 Javadoc 格式的中文注释，包含类描述、作者、创建时间
- 所有公共方法必须使用 Javadoc 格式的中文注释，包含方法描述、参数说明、返回值说明、异常说明
- 复杂业务逻辑必须添加行内中文注释，解释实现思路和关键步骤
- 配置类、工具类、常量类必须详细注释每个字段和方法的用途
- 接口（Interface）必须注释其设计意图和使用场景

**理由**: 中文注释便于国内开发团队理解和维护代码，降低沟通成本，提高代码可读性。中文注释有助于新成员快速理解业务逻辑。

**验证**:
- 代码审查时检查关键类和方法是否有中文注释
- 使用代码质量工具（如 SonarQube）检查注释覆盖率
- 禁止使用无意义的英文注释（如 `// TODO`、`// FIXME` 应改为中文说明）

### 原则 8: 代码复用与公共方法提取

**规则**: 重复代码必须提取为公共方法或工具类，遵循 DRY（Don't Repeat Yourself）原则。

**具体要求**:
- 相同或相似的业务逻辑必须提取到 Service 层的公共方法
- 通用的工具方法必须放在 `utils` 包下的工具类中，方法声明为 `public static`
- 重复的验证逻辑必须提取为公共验证方法或使用注解
- 重复的异常处理逻辑必须使用统一异常处理器（`@ControllerAdvice`）
- 重复的数据转换逻辑必须使用 MapStruct 或自定义转换器
- 公共方法必须添加完整的中文注释，说明使用场景和注意事项

**理由**: 代码复用减少维护成本，提高代码质量，确保业务逻辑的一致性。公共方法便于单元测试和功能扩展。

**验证**:
- 代码审查时识别重复代码模式，要求重构
- 使用代码分析工具（如 PMD、Checkstyle）检测重复代码
- 新增功能时优先检查是否有可复用的公共方法

### 原则 9: 设计模式应用

**规则**: 在适当场景下使用经典设计模式，提高代码的可维护性、可扩展性和可测试性。

**推荐使用的设计模式**:

1. **策略模式（Strategy Pattern）**
   - 适用于：多种算法或业务规则需要动态切换的场景
   - 示例：支付方式选择、数据验证规则、排序策略等

2. **工厂模式（Factory Pattern）**
   - 适用于：对象创建逻辑复杂，需要统一管理的场景
   - 示例：数据源工厂、消息处理器工厂、转换器工厂等

3. **建造者模式（Builder Pattern）**
   - 适用于：创建复杂对象，需要灵活配置的场景
   - 示例：复杂查询条件构建、配置对象构建等

4. **模板方法模式（Template Method Pattern）**
   - 适用于：多个类有相似的算法骨架，但具体步骤不同的场景
   - 示例：数据处理流程、审批流程、报表生成等

5. **观察者模式（Observer Pattern）**
   - 适用于：对象间一对多依赖关系，一个对象状态改变需要通知多个对象的场景
   - 示例：事件发布订阅、消息通知、状态变更监听等

6. **适配器模式（Adapter Pattern）**
   - 适用于：需要适配不同接口或第三方组件的场景
   - 示例：第三方 API 适配、数据格式转换等

7. **单例模式（Singleton Pattern）**
   - 适用于：确保类只有一个实例的场景（谨慎使用，优先使用 Spring 的 Bean 管理）
   - 注意：在 Spring 环境中，通常使用 `@Component` 或 `@Service` 注解即可

8. **责任链模式（Chain of Responsibility Pattern）**
   - 适用于：多个对象可以处理同一请求，但处理优先级不同的场景
   - 示例：权限验证链、数据校验链、审批流程等

**使用原则**:
- **适度使用**: 不要为了使用设计模式而使用，应根据实际业务需求选择
- **保持简单**: 优先使用简单直接的实现方式，只有在复杂度确实需要时才引入设计模式
- **文档说明**: 使用设计模式时，必须在代码注释中说明使用的模式和设计意图
- **团队共识**: 复杂的设计模式使用前应经过团队评审，确保团队成员理解

**避免过度设计**:
- 不要在不必要的地方使用复杂的设计模式
- 不要为了展示技术能力而引入不必要的抽象层
- 优先使用 Spring 框架提供的特性（如依赖注入、AOP）来实现解耦

**理由**: 
- 设计模式是经过验证的解决方案，能够解决常见的软件设计问题
- 适当使用设计模式可以提高代码的可维护性和可扩展性
- 统一的模式使用有助于团队理解和维护代码
- 避免过度设计，保持代码简洁易懂

**验证**:
- 代码审查时检查复杂业务逻辑是否可以使用设计模式优化
- 检查设计模式的使用是否合理，避免过度设计
- 确保设计模式的使用有充分的文档说明

### 原则 10: 配置文件格式规范

**规则**: 所有 resource 目录下的配置文件必须使用 YAML 格式（`.yaml` 或 `.yml` 扩展名），禁止使用 Properties 格式（`.properties`）。

**具体要求**:
- 所有 Spring Boot 配置文件必须使用 `application.yaml` 或 `application.yml`，禁止使用 `application.properties`
- 所有环境配置文件（如 `application-dev.yaml`、`application-prod.yaml`）必须使用 YAML 格式
- 所有自定义配置文件（如日志配置、数据源配置等）必须使用 YAML 格式
- YAML 文件必须遵循标准的 YAML 语法规范，使用 2 个空格缩进
- 配置项 Key 使用小写字母和连字符（kebab-case），如 `spring.application.name`
- 敏感配置（如密码、密钥）应使用环境变量或配置中心（Nacos）管理，避免硬编码

**理由**: 
- YAML 格式比 Properties 格式更易读，支持层级结构和数据类型
- YAML 格式便于管理复杂的配置结构，减少配置文件的复杂度
- 统一的配置格式提高团队协作效率，降低配置错误率
- YAML 格式与 Spring Boot 和 Spring Cloud 配置管理最佳实践一致

**例外情况**:
- 如果第三方组件强制要求使用 Properties 格式，可以在代码注释中说明原因
- 历史遗留的 Properties 配置文件应逐步迁移到 YAML 格式

**验证**:
- 代码审查时检查是否有 Properties 格式的配置文件
- CI/CD 流水线中可以添加检查，禁止提交 Properties 格式的配置文件
- 新创建的配置文件必须使用 YAML 格式

## 项目结构原则

### 原则 11: 模块化设计

**规则**: 项目必须采用模块化设计，遵循分层架构原则。

**标准结构**:
```
atlas/
├── atlas-gateway/        # API 网关
├── atlas-auth/           # 认证授权服务
├── atlas-common/         # 公共模块
│   ├── atlas-common-infra/     # 基础设施模块
│   │   ├── atlas-common-infra-web/      # Web 相关工具
│   │   ├── atlas-common-infra-redis/    # Redis 相关工具
│   │   ├── atlas-common-infra-db/       # 数据库相关工具
│   │   └── atlas-common-infra-logging/  # 日志相关
│   └── atlas-common-feature/   # 功能特性模块
│       ├── atlas-common-feature-core/      # 核心工具类
│       └── atlas-common-feature-security/   # 安全相关
├── atlas-service/        # 服务模块
│   ├── atlas-system/     # 系统管理服务
│   └── ... 
└── atlas-service-api/        # API 接口定义
    ├── atlas-system-api/     # 系统管理服务 API
    └── ...
```

**理由**: 模块化设计便于代码组织、团队协作和功能扩展，符合微服务架构的最佳实践。

**模块文档要求**:
- 每个模块必须在根目录下提供 `README.md` 文件
- `README.md` 必须包含以下内容：
  - 模块简介：说明模块的用途和职责
  - 主要功能：列出模块提供的核心功能
  - 快速开始：提供依赖配置和使用示例
  - 相关文档：链接到详细文档（如 quickstart.md、spec.md 等）
- `README.md` 使用中文编写，遵循项目文档规范

**理由**: 
- 模块文档帮助开发人员快速了解模块功能和使用方法
- 统一的文档格式提高项目可维护性
- 便于新成员快速上手和理解项目结构

**验证**:
- 代码审查时检查新模块是否包含 README.md
- CI/CD 流水线可以添加检查，确保所有模块都有 README.md
- 定期审查模块文档的完整性和准确性

### 原则 12: 模块职责边界

**规则**: 每个模块必须有明确的职责边界，禁止跨边界直接访问，必须通过定义好的接口进行交互。

**模块职责定义**:

1. **atlas-gateway（API 网关）**
   - **职责**: 统一入口、路由转发、负载均衡、限流熔断、跨域处理
   - **允许**: 
     - 路由转发到业务服务（网关的基本功能）
     - 调用认证服务进行鉴权
     - 调用配置中心获取配置
     - 记录访问日志
   - **禁止**: 
     - 包含业务逻辑
     - 直接访问数据库
     - 作为客户端主动聚合调用多个业务服务来拼装业务响应（避免 BFF/编排逻辑进入网关）
     - 业务编排和数据聚合（应通过独立的 BFF 服务或业务服务实现）

2. **atlas-auth（认证授权服务）**
   - **职责**: 用户认证、Token 生成与验证、权限校验、会话管理
   - **禁止**: 
     - 包含业务逻辑
     - 直接访问业务数据
     - 处理非认证授权相关功能
     - 直接依赖 system 的持久层/表结构
   - **允许**: 
     - 访问用户基础信息（必须通过 `atlas-system-api` 或未来的 `atlas-user-api` 提供的接口获取，禁止直接依赖 system 的持久层/表结构）
     - 调用 Redis 存储 Token
     - 调用数据库存储用户凭证（仅限认证授权相关的凭证数据）

3. **atlas-common-infra（基础设施模块）**
   - **atlas-common-infra-web**: Web 相关工具（请求响应处理、参数校验、异常处理等）
   - **atlas-common-infra-redis**: Redis 操作封装、缓存工具类
   - **atlas-common-infra-db**: 数据库操作封装、MyBatis-Plus 配置、数据源管理
   - **atlas-common-infra-logging**: 日志配置、日志工具类、链路追踪
   - **禁止**: 包含业务逻辑、直接依赖业务模块
   - **允许**: 被所有业务模块依赖，提供通用基础设施能力

4. **atlas-common-feature（功能特性模块）**
   - **atlas-common-feature-core**: 核心工具类（日期处理、字符串处理、加密解密、文件操作等）
   - **atlas-common-feature-security**: 安全相关（权限注解、安全工具类、加密工具等）
   - **禁止**: 包含业务逻辑、直接访问数据库、依赖业务服务
   - **允许**: 被所有业务模块依赖，提供通用功能特性

5. **atlas-service（服务模块）**
   - **职责**: 业务逻辑实现、数据处理、业务规则执行、提供 HTTP/gRPC 接口
   - **禁止**: 接口契约未在 API 模块定义、直接调用其他服务的数据库
   - **允许**: 提供 Controller 实现 HTTP 接口（但接口契约必须在 atlas-service-api 中定义）、调用其他服务的 API 接口、使用公共模块提供的工具、访问自己的数据库

6. **atlas-service-api（API 接口定义模块）**
   - **职责**: 定义服务接口（Feign 接口、DTO、常量等）、接口版本管理、接口兼容性保证
   - **禁止**: 包含业务逻辑实现、直接访问数据库
   - **允许**: 定义接口契约、DTO 对象、常量定义、接口文档注解
   - **接口兼容性规则**:
     1. **不允许破坏性变更**: 字段删除、字段改名、字段语义改变属于破坏性变更，禁止在现有版本中进行
     2. **破坏性变更必须走新版本**: 破坏性变更必须通过新版本包名（如 `v1`、`v2`）或新接口路径（如 `/api/v1/users`、`/api/v2/users`）实现
     3. **DTO 新增字段必须向后兼容**: DTO 新增字段必须保持向后兼容，字段必须提供默认值或设置为可空（`@Nullable`），确保旧版本客户端仍能正常使用
     4. **版本管理策略**: 
        - 使用包名版本：`com.atlas.system.api.v1`、`com.atlas.system.api.v2`
        - 或使用路径版本：`/api/v1/users`、`/api/v2/users`
        - 同一服务内应保持一致的版本管理策略

**跨模块交互规则**:

1. **服务间调用**: 
   - **优先使用**: 通过 `atlas-service-api` 中定义的 Feign 接口进行服务间调用
   - **允许例外**: 以下场景允许直接 HTTP 调用
     - 对接第三方系统（支付、短信、邮件等外部服务）
     - 调用网关或外部系统（非业务服务）
     - 迁移、应急排障等特殊场景（需满足收口机制要求，见下方）
   - **禁止**: 业务服务之间直接使用 RestTemplate/OkHttp 等原生 HTTP 客户端进行调用（应使用 Feign）
   - **应急排障收口机制**: 应急排障场景下的直接 HTTP 调用必须满足以下强约束：
     - 必须在代码中添加 TODO 注释：`// TODO(atlas): remove direct http call before <date>`，其中 `<date>` 为具体日期（不超过一个迭代周期）
     - 必须关联 Issue 链接，说明应急原因和回收计划
     - 代码审查时必须要求在一个迭代内回收成 Feign 接口
     - 禁止将应急直连作为长期方案，必须在指定日期前完成迁移

2. **公共模块使用**: 
   - 业务模块可以依赖 `atlas-common-*` 模块
   - 公共模块之间允许单向依赖：`atlas-common-infra-*` 可以依赖 `atlas-common-feature-*`，但 `atlas-common-feature-*` 禁止依赖 `atlas-common-infra-*`
   - **示例**: `atlas-common-infra-web` 可以使用 `atlas-common-feature-core` 的 R、错误码、异常体系；`atlas-common-infra-logging` 可以使用 `atlas-common-feature-core` 的 TraceId 常量

3. **数据库访问**: 每个服务只能访问自己的数据库，禁止跨服务直接访问数据库

4. **配置管理**: 统一使用 Nacos Config 进行配置管理，禁止硬编码配置

5. **依赖方向**: 依赖方向必须单向，禁止循环依赖
   - `atlas-service` → `atlas-service-api` → `atlas-common-*`
   - `atlas-gateway` → `atlas-auth` → `atlas-common-*`
   - `atlas-common-infra-*` → `atlas-common-feature-*`（允许单向依赖）

**模块依赖矩阵**:

以下表格清晰定义了各模块的依赖规则，便于快速查阅和验证：

| 模块 | 可依赖 | 禁止依赖 |
|------|--------|----------|
| `atlas-gateway` | `atlas-common-*`<br>`atlas-auth-api`（可选） | `atlas-service` / `atlas-service-api`（默认禁止） |
| `atlas-auth` | `atlas-common-*`<br>`atlas-system-api` | `atlas-system` 持久层<br>其他 `atlas-service` |
| `atlas-service` | `atlas-common-*`<br>自己的 `atlas-service-api` | 其他 `atlas-service` 持久层 |
| `atlas-service-api` | `atlas-common-feature-core` | `atlas-common-infra-*`<br>Spring/Web/DB/Redis 实现层 |
| `atlas-common-infra-*` | `atlas-common-feature-*`（允许单向） | 任何业务模块 |
| `atlas-common-feature-*` | `atlas-common-feature-core`（可依赖） | `atlas-common-infra-*`<br>任何业务模块 |

**说明**:
- **可依赖**: 该模块允许依赖的模块列表
- **禁止依赖**: 该模块禁止依赖的模块列表，违反此规则将导致架构问题
- **单向依赖**: `atlas-common-infra-*` 可以依赖 `atlas-common-feature-*`，但反向禁止
- **持久层**: 指数据库访问层（Mapper、Entity 等），禁止跨服务直接访问

**职责边界验证**:

- 代码审查时检查模块依赖关系，确保符合依赖方向规则
- 检查是否有跨模块直接访问数据库的情况
- 检查业务服务间调用是否优先使用 Feign 接口，直接 HTTP 调用是否有合理原因
- 检查应急排障场景下的直接 HTTP 调用是否满足收口机制要求（TODO 注释、Issue 链接、回收计划）
- 检查服务接口契约是否在 atlas-service-api 中定义
- 检查 auth 服务访问用户基础信息是否通过 API 接口，禁止直接依赖 system 的持久层/表结构
- 检查 service-api 中的接口变更是否符合兼容性规则（禁止破坏性变更、新增字段必须向后兼容）
- 使用依赖分析工具（如 Maven Dependency Plugin）检查循环依赖
- 确保公共模块不依赖业务模块
- 验证 infra 模块对 feature 模块的依赖是否符合单向依赖规则

**理由**: 
- 明确的职责边界有助于代码组织和团队协作
- 防止模块间耦合，提高代码可维护性和可测试性
- 便于模块独立开发、测试和部署
- 符合单一职责原则和依赖倒置原则

## 质量保证原则

### 原则 13: 单元测试要求

**规则**: 核心业务逻辑和公共方法必须编写单元测试，测试覆盖率不低于 70%。

**理由**: 单元测试确保代码质量，减少回归问题，提高重构信心。

### 原则 14: 代码规范检查

**规则**: 代码必须通过 Checkstyle、PMD、SpotBugs 等静态代码分析工具检查。

**理由**: 统一的代码风格和规范提高代码可读性和可维护性。

## 治理规则

### 版本管理

**版本号规则**: 遵循语义化版本（SemVer）规范：`MAJOR.MINOR.PATCH`

- **MAJOR**: 不兼容的 API 修改或重大架构变更
- **MINOR**: 向后兼容的功能新增
- **PATCH**: 向后兼容的问题修复

**宪法版本更新规则**:
- **MAJOR**: 移除或重新定义原则，导致向后不兼容的治理变更
- **MINOR**: 新增原则或章节，或显著扩展现有原则的指导内容
- **PATCH**: 澄清说明、措辞修正、非语义性改进

### 修订程序

1. **提案**: 任何团队成员可以提出宪法修订提案，说明修订理由和影响范围
2. **讨论**: 在团队会议或代码审查中讨论提案，评估对现有代码和流程的影响
3. **批准**: 需要至少 2 名核心成员批准，重大修订需要团队一致同意
4. **更新**: 更新宪法文件，更新版本号和最后修订日期
5. **传播**: 更新相关模板文件（plan-template.md、spec-template.md、tasks-template.md 等），确保一致性
6. **通知**: 通知所有团队成员，更新项目文档

### 合规审查

- **代码审查**: 每次 Pull Request 必须检查是否符合宪法原则
- **定期审查**: 每季度进行一次宪法合规性审查，识别违反原则的代码和技术债务
- **工具检查**: 使用自动化工具（CI/CD）检查技术栈版本、代码规范、测试覆盖率等
- **文档更新**: 宪法修订后，相关技术文档和开发指南必须同步更新

### 例外处理

在特殊情况下（如紧急修复、技术限制），可以申请临时例外，但必须：
1. 在代码注释中明确说明例外原因和预期解决时间
2. 在项目 Issue 中记录例外情况
3. 在下次合规审查中评估是否可以将例外情况标准化或消除

## 附录

### 参考资源

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Spring Cloud 官方文档](https://spring.io/projects/spring-cloud)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [RESTful API 设计指南](https://restfulapi.net/)
- [语义化版本规范](https://semver.org/lang/zh-CN/)

### 变更历史

| 版本 | 日期 | 修订内容 | 修订人 |
|------|------|----------|--------|
| 0.1.0 | 2025-01-27 | 初始版本创建 | 系统 |
| 0.1.1 | 2025-01-27 | 更新项目标准结构，调整模块组织方式 | 系统 |
| 0.2.0 | 2025-01-27 | 新增数据库技术选型、组件优先使用 Spring Cloud 生态、设计模式应用原则 | 系统 |
| 0.2.1 | 2025-01-27 | 修改数据库连接配置为仅使用 MyBatis-Plus，删除数据源配置要求 | 系统 |
| 0.2.2 | 2025-01-27 | 将 atlas-common 子模块拆分为 atlas-common-infra 和 atlas-common-feature | 系统 |
| 0.2.3 | 2025-01-27 | 优化项目结构，修复缩进和注释，修正模块名称拼写错误 | 系统 |
| 0.2.4 | 2025-01-27 | 移除 atlas-admin 模块 | 系统 |
| 0.3.0 | 2025-01-27 | 新增模块职责边界原则，定义各模块职责和交互规则 | 系统 |
| 0.3.1 | 2025-01-27 | 调整模块职责边界：允许公共模块单向依赖、调整服务接口定义规则、服务间调用允许例外 | 系统 |
| 0.3.2 | 2025-01-27 | 明确 gateway 允许路由转发但禁止业务编排和聚合调用 | 系统 |
| 0.3.3 | 2025-01-27 | 明确 auth 访问用户信息的边界，添加 service-api 接口兼容性规则 | 系统 |
| 0.3.4 | 2025-01-27 | 为应急排障直接 HTTP 调用添加收口机制（TODO 注释、Issue 链接、迭代内回收） | 系统 |
| 0.3.5 | 2025-01-27 | 新增模块依赖矩阵表格，便于快速查阅和验证依赖关系 | 系统 |
| 0.3.6 | 2026-01-05 | 新增配置文件格式规范（resource 中配置文件使用 yaml 格式） | 系统 |
| 0.3.7 | 2026-01-05 | 新增模块文档要求（每个模块下需要有 README.md） | 系统 |

---

**注意**: 本宪法是项目的根本性指导文档，所有开发活动必须遵循本宪法的规定。如有疑问或建议，请通过修订程序提出。
