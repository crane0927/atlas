# 研究文档：宪法原则 20/21 合规改造

## 技术决策

### 1. 对象转换方式（原则 20）

**决策**：优先使用 Spring `BeanUtils.copyProperties(source, target)` 完成 Entity/DTO/VO 同名字段拷贝；仅在字段名或类型不一致、或需忽略字段时，在工具方法中保留少量手写映射并注释原因。

**理由**：
- 项目已使用 Spring Boot，`BeanUtils` 来自 `spring-beans`，无新增依赖。
- 与宪法“禁止手写逐字段赋值”一致，且改造面可控；MapStruct 需新增 Mapper 接口与编译期生成，本需求优先采用 BeanUtils 以快速合规。
- 若某模块已使用 MapStruct，可保持既有 Mapper，不强制改为 BeanUtils。

**备选**：
- MapStruct：适合字段多、有复杂映射或多次复用的场景；父 POM 已管理版本，后续可对高频转换引入 MapStruct Mapper。
- Apache BeanUtils：与 Spring BeanUtils 类似，但项目统一使用 Spring 生态，不引入。

### 2. 参数与空值处理方式（原则 21）

**决策**：
- **必填/前置条件**：使用 `org.springframework.util.Assert`（如 `Assert.notNull(obj, "message")`、`Assert.hasText(str, "message")`），违反时抛出 `IllegalArgumentException` 等，与现有全局异常处理一致。
- **可选/默认值**：使用 `Optional.ofNullable(x).map(MyDTO::getY).orElse(default)` 替代 `x == null ? default : x.getY()`，减少 if-else 分支。

**理由**：
- Spring Assert 与 Spring Boot 项目天然契合，无需新依赖。
- Optional 为 JDK 标准，表达“可能为空”的语义清晰，减少 NPE 与多层判空。

**备选**：
- Guava Preconditions：功能类似 Assert，但引入额外依赖；在已使用 Guava 的模块可考虑，否则统一用 Spring Assert。
- 保留 if-else 但抽取工具方法：可读性提升有限，不符合原则 21“尽量使用断言工具或 Optional”的要求。

### 3. 改造范围与顺序

**决策**：先改造 atlas-system 内 user、role、permission、settings 相关 Service 中的转换与分页/查询判空逻辑，再根据清单扩展至其他模块。

**理由**：atlas-system 为高可见度业务模块，且近期分页查询已集中在此，优先改造可快速满足合规并形成范例。

## 依赖与约束

- 不引入新依赖版本；MapStruct 已由父 POM 管理，本需求不强制启用。
- 改造后对外请求/响应、状态码、错误信息保持不变，仅实现方式变更。
- 单元测试按宪法“非必要不生成”执行，本需求以接口级或手工回归为主。
