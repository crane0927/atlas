# 研究记录

## 决策 1: 数据库迁移工具

- **Decision**: 使用 Flyway 管理迁移脚本
- **Rationale**: 现有 `atlas-system` 已使用 Flyway 迁移目录与命名方式，保持一致性成本最低
- **Alternatives considered**: Liquibase（需要额外维护变更记录格式）

## 决策 2: 设置项类型表达

- **Decision**: 使用枚举值表示类型（SYSTEM、CUSTOM）
- **Rationale**: 与业务规则直接对应，便于校验“系统类型不可删除”
- **Alternatives considered**: 布尔字段 `isSystem`（可读性较差，扩展性弱）

## 决策 3: 资源路径设计

- **Decision**: 使用 `/api/v1/system-settings` 作为资源路径
- **Rationale**: 名词复数，符合 RESTful；覆盖查询、创建、更新、删除
- **Alternatives considered**: `/api/v1/system-settings/defaults`（路径冗长，资源含义重复）

## 决策 4: 权限校验复用点

- **Decision**: 复用 `atlas-common-feature-security` 提供的权限注解与安全上下文抽象
- **Rationale**: 已定义 `@RequiresRole` / `@RequiresPermission` 与 `SecurityContextHolder` 抽象，可用于系统管理员权限控制与登录态获取
- **References**: `atlas-common/atlas-common-feature/atlas-common-feature-security/README.md`
