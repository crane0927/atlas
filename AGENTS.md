# Atlas 项目规则

| 属性 | 值 |
|------|-----|
| **项目** | Atlas — Spring Boot / Spring Cloud 企业级微服务快速开发框架 |
| **规则版本** | 1.1.0 |
| **最后修订** | 2026-06-09 |

> 本文档为 Atlas 开发与 AI 辅助编码的强制性项目规则。冲突时以本文档为准；例外须走 [例外处理](#例外处理) 流程。

---

## 目录

- [一、技术栈](#一技术栈)
- [二、架构与设计](#二架构与设计)
- [三、项目结构](#三项目结构)
- [四、质量保证](#四质量保证)
- [五、治理](#五治理)

---

## 一、技术栈

### 1. Java 版本

- **必须**使用 JDK 21（唯一支持版本）；`pom.xml` 明确指定；CI 使用 JDK 21 构建。
- **禁止**使用已废弃 API 或非 JDK 21 特性。

### 2. Spring Boot

- **必须**使用 Spring Boot **3.5.9**；版本由父 POM 统一管理。
- **禁止**在子模块显式指定 Spring Boot 依赖版本。

### 3. Spring Cloud 生态

- **必须**使用 Spring Cloud **2025.0.1**、Spring Cloud Alibaba **2025.0.0.0**；版本由 BOM 管理。

### 4. 数据库

| 项 | 要求 |
|----|------|
| 主库 | PostgreSQL |
| ORM | MyBatis-Plus |
| 迁移 | Flyway |
| 辅助存储 | Redis 等仅作缓存或特定场景 |
| 禁止 | MySQL/Oracle 作主库；Spring Data JPA 及其他 ORM |
| 豁免 | 网关、配置中心等无数据源模块无需配置数据源 |

### 5. 组件选型（Spring Cloud 生态优先）

| 能力 | 使用 | 避免 |
|------|------|------|
| 注册发现 | Nacos | Eureka、Consul |
| 配置 | Nacos Config | Spring Cloud Config Server |
| 网关 | Spring Cloud Gateway | Zuul、Kong |
| 服务调用 | OpenFeign | RestTemplate、OkHttp（业务服务间） |
| 熔断/限流 | Sentinel | Hystrix、Guava RateLimiter |
| 分布式事务 | Seata | 其他方案 |
| 消息队列 | RocketMQ | RabbitMQ、Kafka |
| 链路追踪 | SkyWalking 或 Micrometer Tracing | Zipkin、Jaeger |
| 负载均衡 | Spring Cloud LoadBalancer | — |

- 生态无对应组件时，可引入第三方，**须**在代码/文档说明原因并经团队评审。

---

## 二、架构与设计

### 6. RESTful API

- 标准 HTTP 方法；URL 用名词复数（`/api/v1/users`），**禁止**动词路径（`/getUsers`）。
- 响应 JSON，统一 `Result<T>` 包装；分页参数 `page`、`size`，排序 `sort`。
- Service **禁止**返回 `Result`；Controller 负责包装。

### 7. 代码注释

- 公共类/方法**必须**中文 Javadoc（类描述、参数、返回值、异常）。
- 复杂逻辑、配置类、工具类、接口**须**中文注释说明意图。
- `TODO`/`FIXME` 使用中文说明。

### 8. 代码复用（DRY）

- 重复业务逻辑提取至 Service 公共方法；通用工具放 `utils` 包（`public static`）。
- 重复校验用注解或公共方法；异常统一 `@ControllerAdvice`；转换用 MapStruct/转换器。

### 9. 设计模式

- **适度使用**：策略、工厂、建造者、模板方法、观察者、适配器、责任链等；Spring 环境优先 `@Component`/`@Service` 而非手写单例。
- **禁止**为模式而模式、过度抽象。
- 使用设计模式时，类级 Javadoc **必须**注明：模式名称、使用原因。
- 复杂模式使用前经团队评审。

### 10. 配置文件

- `resources` 下**必须** YAML（`.yaml`/`.yml`），**禁止** `.properties`（第三方强制要求除外，须注释说明）。
- 2 空格缩进；Key 用 kebab-case；敏感项走环境变量或 Nacos。

---

## 三、项目结构

### 11. 模块化

```
atlas/
├── atlas-gateway/              # API 网关
├── atlas-auth/                 # 认证授权
├── atlas-common/
│   ├── atlas-common-infra/     # web / redis / db / logging
│   └── atlas-common-feature/   # core / security
├── atlas-service/              # 业务服务（如 atlas-system）
└── atlas-service-api/          # Feign 契约与 DTO
```

- 每个模块根目录**须**有中文 `README.md`（简介、主要功能、快速开始、相关文档链接）。

### 12. 包结构

| 模块类型 | 组织方式 | 包路径 |
|----------|----------|--------|
| 业务模块 | 业务 → 技术分层 | `com.atlas.{module}.{business}.{layer}` |
| 技术模块（独立） | 技术分层 | `com.atlas.{module}.{layer}` |
| 技术模块（公共） | 技术分层 | `com.atlas.common.{category}.{module}.{layer}` |

示例：`com.atlas.system.user.controller`；`com.atlas.gateway.filter`；`com.atlas.common.infra.web.config`。

### 13. 数据对象与 model 层

**所有对象类型（DTO、VO、Entity、BO、枚举等）必须在 `model` 包下定义。**

#### 对象职责

| 类型 | 用途 | 包路径（业务） | 包路径（API） |
|------|------|----------------|---------------|
| Entity | 持久化，Mapper/Service 内部 | `...model.entity` | **禁止** |
| DTO | 跨层传输、Feign 契约 | `...model.dto` | `...api.v{n}.model.dto` |
| VO | 前后端交互、展示 | `...model.vo` | **禁止** |
| BO | 复杂业务（可选） | `...model.bo` | **禁止** |
| 枚举 | 状态/类型 | `...model.enums` | `...api.v{n}.model.enums` |
| Query/Request | 查询条件 | VO→`model.vo`；DTO→`model.dto` | DTO only |

命名：`UserDTO`、`UserVO`、`UserQueryVO`、`UserQueryDTO`、`UserStatus`。

#### 数据流转

```
前端 → Controller(VO) → Service(DTO) → Mapper(Entity) → DB
DB → Mapper(Entity) → Service(DTO) → Controller(VO) → Result<VO> → 前端
服务A → Feign(DTO) → 服务B → ... → Result<DTO> → 服务A
```

#### 响应包装

| 类 | 包路径 | 规则 |
|----|--------|------|
| `Result<T>` | `com.atlas.common.feature.core.result` | 所有 HTTP/Feign 响应 |
| `PageResult<T>` | `com.atlas.common.feature.core.page` | 分页接口 |

#### 禁止事项

- API 模块定义 VO/Entity/BO；Feign 使用 VO/Entity。
- Controller 直接接收或返回 Entity。
- Service 返回 `Result`/`PageResult`。
- 在 `model` 包外定义对象类型；枚举放在 `model.enums` 外。

#### API 兼容性

- 同版本**禁止**删字段、改字段名、改字段语义。
- 破坏性变更走新版本（`v2` 包或 `/api/v2/...`）。
- DTO 新增字段须可空或有默认值（`@Nullable`）。

### 14. 模块职责与依赖

#### 职责摘要

| 模块 | 职责 | 禁止 |
|------|------|------|
| `atlas-gateway` | 路由、限流、鉴权、跨域 | 业务逻辑、访问 DB、BFF 编排 |
| `atlas-auth` | 认证、Token、权限、会话 | 业务逻辑、依赖 system 持久层 |
| `atlas-common-infra-*` | 基础设施 | 业务逻辑、依赖业务模块 |
| `atlas-common-feature-*` | 通用功能 | 业务逻辑、访问 DB、依赖业务服务 |
| `atlas-service` | 业务实现 | 契约未在 API 定义、跨服务访问 DB |
| `atlas-service-api` | Feign、DTO、版本管理 | 业务实现、访问 DB |

- `atlas-auth` 获取用户信息**须**通过 `atlas-system-api`（或 `atlas-user-api`），**禁止**直接依赖 system 持久层。

#### 依赖矩阵

| 模块 | 可依赖 | 禁止依赖 |
|------|--------|----------|
| `atlas-gateway` | `atlas-common-*`、`atlas-auth-api`（可选） | `atlas-service` / `atlas-service-api`（默认） |
| `atlas-auth` | `atlas-common-*`、`atlas-system-api` | system 持久层、其他 `atlas-service` |
| `atlas-service` | `atlas-common-*`、自有 `*-api` | 其他服务持久层 |
| `atlas-service-api` | `atlas-common-feature-core` | `atlas-common-infra-*`、Web/DB/Redis 实现 |
| `atlas-common-infra-*` | `atlas-common-feature-*` | 任何业务模块 |
| `atlas-common-feature-*` | `atlas-common-feature-core` | `atlas-common-infra-*`、任何业务模块 |

#### 跨模块规则

- 服务间调用**优先** Feign（`atlas-service-api`）；业务服务间**禁止** RestTemplate/OkHttp。
- 允许直连 HTTP：第三方、网关/外部系统、应急排障（须满足收口机制）。
- 每服务仅访问自有数据库；配置统一 Nacos Config；**禁止**循环依赖。

**应急排障收口**：代码添加 `// TODO(atlas): remove direct http call before <date>`；关联 Issue；一个迭代内回收为 Feign。

### 15. 数据库实体

- Entity **必须**继承 `com.atlas.common.infra.db.entity.BaseEntity`。
- **禁止**重复定义 `deleted`、`createdAt`、`updatedAt`、`createdBy`、`updatedBy`。
- 字段名差异通过注解映射，仍须继承 `BaseEntity`。

### 16. Dockerfile

可独立启动的微服务**必须**在模块根目录提供：

```
{module}/docker/Dockerfile.build   # JDK 21 + Maven/Gradle 构建
{module}/docker/Dockerfile.run     # JRE/JDK 21 运行，暴露端口，建议 HEALTHCHECK
```

- `atlas-common-*`、`atlas-service-api` 等不可启动模块**无需** Dockerfile。

---

## 四、质量保证

### 17. 单元测试

- 核心业务与公共方法覆盖率目标 ≥ 70%。
- **AI 辅助开发默认不生成单元测试**；仅在用户明确要求、核心逻辑经确认需测、或 Bug 回归时生成。

### 18. 静态检查

- 代码**必须**通过 Checkstyle、PMD、SpotBugs。

### 19. 对象转换

- Entity/DTO/VO 转换**必须**用 `BeanUtils.copyProperties`、MapStruct 或项目既定框架。
- **禁止**大段手写 `setXxx(getXxx())`；转换逻辑集中在工具方法或 Mapper 接口。

### 20. 参数与空值

- 前置校验优先 `Assert.notNull`/`Assert.hasText` 或项目统一断言工具。
- 可空语义优先 `Optional`；**避免**多层 if-else 判空嵌套。

---

## 五、治理

### 版本管理

- 项目版本遵循 [SemVer](https://semver.org/lang/zh-CN/)：`MAJOR.MINOR.PATCH`。
- 本规则文档版本：MAJOR=原则移除/重定义；MINOR=新增原则或显著扩展；PATCH=澄清与非语义修正。

### 修订程序

提案 → 讨论 → 至少 2 名核心成员批准（重大变更须一致同意）→ 更新文档与版本 → 同步相关文档 → 团队通知。

### 合规审查

| 类型 | 频率 |
|------|------|
| PR 代码审查 | 每次 |
| 原则合规与债务 | 每季度 |
| CI 工具检查 | 持续 |
| 文档同步 | 规则修订后 |

### 例外处理

紧急修复或技术限制可申请临时例外，**须**：代码注释说明原因与解决时间；Issue 记录；下次合规审查评估是否消除。

---

## 附录

### 参考

| 资源 | 链接 |
|------|------|
| Spring Boot | <https://spring.io/projects/spring-boot> |
| Spring Cloud | <https://spring.io/projects/spring-cloud> |
| MyBatis-Plus | <https://baomidou.com/> |
| RESTful | <https://restfulapi.net/> |

### 变更历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2026-02-09 | 正式发布，原则 1–20 |
| 1.1.0 | 2026-06-09 | 重构为精炼项目规则：去除冗余理由/验证，统一 MUST/禁止表述，表格化核心约束 |
