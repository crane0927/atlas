# 功能规格说明：根据宪法原则 20 和 21 调整判断逻辑与转换逻辑

## 宪法检查

本规格说明必须符合项目宪法要求：

- ✅ **RESTful API**: 不新增接口，仅调整既有实现
- ✅ **中文注释**: 所有代码使用中文注释
- ✅ **代码复用**: 通过 BeanUtils/MapStruct 统一转换方式
- ✅ **模块化**: 调整范围限于现有业务与公共模块
- ✅ **实体继承**: 不变更 Entity 定义
- ✅ **对象转换**: 使用 BeanUtils 或 MapStruct 进行 Entity/DTO/VO 转换，禁止手写逐字段赋值
- ✅ **参数与空值**: 参数校验与空值处理使用 Assert 或 Optional

## 功能描述

对现有代码进行合规性改造，使**对象转换**与**参数/空值处理**符合宪法原则 20 和 21：

- **原则 20（对象转换规范）**：将当前手写逐字段赋值的 Entity/DTO/VO 转换（例如 Service 中的 `convertToListVO`、`convertToDTO` 等）改为使用 BeanUtils（如 Spring `BeanUtils.copyProperties`）或 MapStruct 等框架，避免遗漏字段并与实体变更同步。
- **原则 21（参数与空值处理规范）**：将冗长的 `if (x == null)` / `if (query != null && ...)` 等判空与校验逻辑，改为使用断言工具（如 Spring `Assert`、Guava `Preconditions`）或 `Optional`，减少分支嵌套并统一异常与语义。

改造不改变对外 API 行为与业务结果，仅调整实现方式以符合宪法并提升可维护性。

## 范围与边界

- **在范围内**：
  - atlas-system 及项目中其他存在 Entity/DTO/VO 手写转换的模块。
  - 存在参数判空、默认值赋值的分页/查询/服务方法，改为 Assert 或 Optional。
- **不在范围内**：
  - 新增业务功能或新接口。
  - 修改 API 契约、请求/响应结构或数据库表结构。
  - 引入新的转换框架版本或新依赖的选型决策（在现有 BeanUtils/MapStruct/Assert 约定下实施）。

## 用户场景

| 场景 | 描述 | 验收方式 |
|------|------|----------|
| 开发维护 | 开发者在修改实体或 DTO 时，转换逻辑通过公共工具自动覆盖字段，减少遗漏与重复代码 | 代码审查无手写逐字段转换 |
| 代码审查 | 审查者能通过 Assert/Optional 快速理解前置条件与空值语义，无需追踪多层 if-else | 判空/校验使用 Assert 或 Optional |
| 回归验证 | 改造后接口行为与改造前一致，无功能回归 | 现有接口行为与响应一致 |

## 功能需求

- **FR-1 对象转换合规（原则 20）**
  - 识别并替换所有手写逐字段赋值的 Entity→VO、Entity→DTO、DTO→VO 转换方法。
  - 使用 BeanUtils（如 `BeanUtils.copyProperties(source, target)`）或既有 MapStruct 映射完成转换；若存在字段名/类型不一致或需忽略字段，在工具方法或 MapStruct 配置中集中说明。
  - 转换逻辑集中在工具方法或 Mapper 中，不在 Service 中散落手写 set/get。

- **FR-2 参数与空值处理合规（原则 21）**
  - 对分页/查询等入参的判空与合法性校验，优先使用 Spring `Assert`（如 `Assert.notNull`、`Assert.hasText`）或项目统一断言工具，在违反时抛出明确异常。
  - 对“可能为空”的取值与默认值逻辑，优先使用 `Optional.ofNullable(...).orElse(...)` 或等价写法，减少多层 if-else 判空分支。
  - 在保持可读性的前提下，不引入不必要的 Optional 链；仅替换明显冗长的 if-else 判空逻辑。

- **FR-3 行为不变**
  - 改造后对外接口的请求/响应语义、状态码、错误信息与改造前一致。
  - 分页与查询的默认值（如 page、size）及排序行为保持不变。

## 成功标准

- 所有已改造的 Entity/DTO/VO 转换均通过 BeanUtils 或 MapStruct 完成，代码库中不存在大段手写 setXxx(getXxx()) 的转换方法。
- 已改造的入参校验与空值处理均使用 Assert 或 Optional，无冗长 if-else 判空链。
- 现有相关接口的自动化或手工回归通过，无功能回归。
- 代码审查能依据原则 20、21 对新增与修改代码进行合规检查。

## 假设与依赖

- 项目已允许使用 Spring `BeanUtils` 及现有 MapStruct（若已引入）；不强制在本需求中引入新框架。
- 改造按模块/包分批进行，优先改造 atlas-system 中高可见度的分页与查询服务。
- 测试以现有接口级验证或手工验证为主，不强制要求新增单元测试，除非涉及复杂分支逻辑。

## 实现注意事项

- 改造前先全局检索手写 `convertTo`、`toVO`、`toDTO` 及 `setXxx(getXxx())` 模式，列出清单再逐处替换。
- 使用 BeanUtils 时注意源与目标类型一致或可赋值，必要时保留少量手写字段映射并注释原因。
- 使用 Assert 时统一异常类型与消息风格，与现有全局异常处理保持一致。
- 改造后运行现有测试或接口回归，确认无行为变化。
