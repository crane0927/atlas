# 功能规划文档：根据宪法原则 20 和 21 调整判断逻辑与转换逻辑

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 3.5.9, Spring Cloud 2025.0.1, Spring Cloud Alibaba 2025.0.0.0
- ✅ **数据库**: PostgreSQL + MyBatis-Plus
- ✅ **API 设计**: 不新增接口，仅调整既有实现
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 通过 BeanUtils/MapStruct 统一转换方式
- ✅ **模块化**: 调整范围限于现有业务与公共模块
- ✅ **实体继承**: 不变更 Entity 定义
- ✅ **对象转换**: Entity/DTO/VO 转换使用 BeanUtils 或 MapStruct，禁止手写逐字段赋值（原则 20）
- ✅ **参数与空值**: 参数校验与空值处理优先使用 Assert 或 Optional，避免冗长 if-else（原则 21）
- ✅ **单元测试**: 非必要不生成，本需求以回归验证为主

## 功能概述

对现有代码进行合规性改造，使对象转换与参数/空值处理符合宪法原则 20 和 21，不改变对外 API 行为。目标：

- **原则 20**：将手写逐字段赋值的 Entity/DTO/VO 转换改为使用 Spring BeanUtils 或 MapStruct。
- **原则 21**：将冗长的 if-else 判空与校验改为 Spring Assert 或 Optional。

优先改造 atlas-system 中的分页与查询相关 Service，再视情况覆盖其他模块。

## 技术方案

### 架构设计

- **涉及模块**：atlas-service/atlas-system（user、role、permission、settings 等包下的 Service 实现类）。
- **不改动**：Controller 接口签名、API 契约、Entity/DTO/VO 类定义、数据库与 Mapper。
- **改动点**：
  1. Service 层中的 `convertToXxx`、`toVO`、`toDTO` 等手写转换方法 → 使用 BeanUtils.copyProperties 或 MapStruct Mapper。
  2. 分页/查询方法中的 `query != null ? query.getPageSafe() : 1` 及类似判空 → 使用 Optional 或 Assert（视语义：必填用 Assert，可选默认值用 Optional）。

### 技术选型

| 用途 | 选型 | 说明 |
|------|------|------|
| 对象转换 | Spring BeanUtils.copyProperties | 已随 Spring Boot 提供，同名字段拷贝；字段名/类型不一致时在工具方法中补一手写并注释 |
| 对象转换（可选） | MapStruct | 父 POM 已管理 1.6.2，若某模块已有 MapStruct 可沿用；本需求优先 BeanUtils 以减小改动面 |
| 断言 | org.springframework.util.Assert | notNull、hasText、isTrue 等，与现有异常体系一致 |
| 空值/默认值 | java.util.Optional | ofNullable(...).orElse(...)，表达“可能为空”的默认值逻辑 |

## 实施计划

### 阶段 1：扫描与清单

- 全局检索手写转换：`convertTo`、`toVO`、`toDTO`、成段 `setXxx(getXxx())`，列出文件与方法清单。
- 全局检索分页/查询中的判空：`query != null`、`pageQuery != null`、`x == null ? default : x`，列出文件与方法清单。
- 输出：改造清单（文件路径、方法名、改造类型：转换 / 判空）。

### 阶段 2：对象转换改造（原则 20）

- 对清单中的每个手写转换方法：用 `BeanUtils.copyProperties(source, target)` 替代逐字段 set；若存在字段名或类型不一致或需忽略字段，在工具方法中单独处理并注释原因。
- 将转换逻辑收敛到私有方法或独立工具类，避免在业务方法中散落 set/get。
- 改造后运行相关用例或接口回归，确认行为不变。

### 阶段 3：参数与空值处理改造（原则 21）

- 对分页/查询入参：若为必填（如 Controller 已保证非空），Service 内可用 `Assert.notNull(query, "query 不能为空")` 表达前置条件；若为可选并需默认值，使用 `Optional.ofNullable(query).map(QueryDTO::getPageSafe).orElse(1)` 等形式替代 `query != null ? query.getPageSafe() : 1`。
- 对“可能为空”的返回值或中间变量，在可读性允许的前提下用 Optional 替代多层 if-else。
- 统一使用 Spring Assert 抛出异常，与现有全局异常处理保持一致。

### 阶段 4：验证与收尾

- 运行现有测试或手工调用既有分页/查询接口，确认响应与改造前一致。
- 代码审查：确认无大段手写 set/get 转换、无冗长 if-else 判空链。

## 风险评估

| 风险 | 影响 | 应对 |
|------|------|------|
| BeanUtils 拷贝了不应暴露的字段 | 中 | 仅对同构或已确认安全的 DTO/VO 使用 copyProperties；敏感字段在目标类中不存在或忽略，必要时忽略属性 |
| Assert 抛出异常类型与现有不一致 | 低 | 统一使用 Spring Assert，其抛出 IllegalArgumentException 等，与现有异常处理兼容 |
| 改造引入行为差异 | 高 | 每批改造后做接口级或用例回归，确保默认值、排序、筛选与改造前一致 |

## 验收标准

- 已改造的 Entity/DTO/VO 转换均通过 BeanUtils（或既有 MapStruct）完成，代码库中无大段手写 setXxx(getXxx()) 的转换方法。
- 已改造的入参校验与空值处理均使用 Assert 或 Optional，无冗长 if-else 判空链。
- 现有相关接口的自动化或手工回归通过，无功能回归。
- 代码审查能依据原则 20、21 对改动进行合规检查。
