# 调研记录

## 决策

### 自动配置入口

- **Decision**: 使用 Spring Boot 官方自动配置入口机制提供异常处理自动注册
- **Rationale**: 与框架规范一致，服务侧无需显式导入配置，便于统一治理
- **Alternatives considered**: 业务服务显式 `@Import` 配置类（会增加重复维护成本，且容易遗漏）

### 条件加载与降级

- **Decision**: 使用条件加载避免依赖缺失导致启动失败
- **Rationale**: 公共模块缺失时允许服务降级启动，符合“不可用不阻断”的需求
- **Alternatives considered**: 强制依赖加载失败即终止启动（不符合需求）
