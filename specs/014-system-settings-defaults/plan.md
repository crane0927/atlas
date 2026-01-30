# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 3.5.9, Spring Cloud 2025.0.1, Spring Cloud Alibaba 2025.0.0.0
- ✅ **API 设计**: 遵循 RESTful 风格
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 识别并复用现有公共方法
- ✅ **模块化**: 功能归属 `atlas-service/atlas-system`
- ✅ **数据库技术**: PostgreSQL + MyBatis-Plus + Flyway/Liquibase
- ✅ **SQL 目录管理**: 服务目录下建立 `sql` 目录并按版本管理
- ✅ **实体继承**: 数据库实体必须继承 `BaseEntity`

**宪法合规性评估**:
- ✅ 不引入新技术栈，版本与宪法一致
- ✅ 归属 `atlas-system` 业务模块，符合模块化原则
- ✅ 所有实体继承 `BaseEntity`，复用审计字段与逻辑删除能力
- ✅ 使用 PostgreSQL + MyBatis-Plus + Flyway 管理迁移
- ✅ 接口遵循 RESTful，统一返回 `Result<T>`

## 功能概述

在 `atlas-system` 服务中新增“系统默认设置”管理能力，支持 key-value 形式的配置维护。系统类型设置可修改 value 但不可删除，自定义类型允许新增、修改与删除，确保内置默认设置可保留且安全可控。

**核心价值**:
1. **安全性**: 系统默认设置不可删除，避免误操作
2. **可维护性**: 配置集中管理，支持快速定位与修改
3. **可审计性**: 变更记录操作者与时间

## 技术方案

### 架构设计

**模块归属**: `atlas-service/atlas-system`

**包结构**（按业务再按技术分层）:
- `com.atlas.system.settings.controller`
- `com.atlas.system.settings.service`
- `com.atlas.system.settings.mapper`
- `com.atlas.system.settings.model.entity`
- `com.atlas.system.settings.model.dto`
- `com.atlas.system.settings.model.vo`

**核心组件**:
1. **Controller 层**: 提供设置项的查询、创建、修改、删除接口
2. **Service 层**: 实现类型校验、权限校验、唯一性校验、审计记录
3. **Mapper 层**: 基于 MyBatis-Plus 的数据访问
4. **Entity 层**: 设置项实体继承 `BaseEntity`

### 技术选型

- **数据库**: PostgreSQL（符合宪法要求）
- **ORM**: MyBatis-Plus（复用 `atlas-common-infra-db` 配置）
- **迁移工具**: Flyway（与现有 `atlas-system` 使用方式一致）
- **接口风格**: RESTful + `Result<T>` 包装

## 实施计划

### 阶段 0: 研究与准备

**目标**: 确认现有模块结构与数据库迁移策略

**任务**:
1. 确认 `atlas-system` 现有数据库迁移方式与命名规范
2. 确认现有通用异常与权限校验方式可复用

**输出**: `research.md`

**验收标准**:
- ✅ 迁移工具与模块结构确认完毕
- ✅ 复用点与约束记录清晰

### 阶段 1: 数据模型与迁移脚本

**目标**: 明确设置项数据结构并完成迁移脚本

**任务**:
1. 设计设置项实体与字段约束（含类型与唯一性）
2. 生成 `data-model.md`
3. 创建 Flyway 迁移脚本并更新 `sql/` 目录

**输出**:
- `data-model.md`
- `sql/vX.Y.Z/` 目录与脚本

**验收标准**:
- ✅ 字段与约束满足功能需求
- ✅ key 唯一性约束明确

### 阶段 2: 接口与业务逻辑

**目标**: 完成 CRUD 接口与业务规则

**任务**:
1. 实现查询、创建、修改、删除接口
2. 系统类型禁止删除，仅允许修改 value
3. 自定义类型允许新增、修改、删除
4. 完成接口契约文档 `contracts/`

**输出**:
- Controller/Service/Mapper/Entity 代码
- `contracts/README.md`

**验收标准**:
- ✅ 规则校验完整，错误提示清晰
- ✅ 接口符合 RESTful 规范

### 阶段 3: 测试与文档

**目标**: 覆盖主要业务流程并完善使用文档

**任务**:
1. 单元测试覆盖核心校验逻辑
2. 集成测试覆盖完整流程
3. 更新 `quickstart.md`

**输出**:
- 测试用例
- `quickstart.md`

**验收标准**:
- ✅ 测试覆盖率 ≥ 70%
- ✅ 关键场景测试通过

## 风险评估

1. **规则误用风险**
   - **风险**: 系统类型误删/误改字段
   - **应对**: 严格校验类型与可操作字段

2. **数据一致性风险**
   - **风险**: key 重复导致数据混乱
   - **应对**: DB 唯一性约束 + 业务校验

3. **权限风险**
   - **风险**: 非管理员操作设置项
   - **应对**: 复用现有权限校验机制

## 验收标准

1. 系统类型设置项不可删除，仅可修改 value
2. 自定义类型设置项可新增、修改、删除
3. key 唯一性校验生效且提示清晰
4. 变更记录包含操作者与时间
5. 接口遵循 RESTful，返回统一 `Result<T>`
