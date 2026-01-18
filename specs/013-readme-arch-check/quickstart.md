# 快速开始

## 前置条件

- 项目内 README.md 可访问
- 具备当前项目架构规范参考文档

## 使用方式

- 汇总所有模块 README 清单（见下方清单）
- 对照项目宪法与模块职责边界逐一核对一致性
- 记录核对结论与差异点到 `README-audit.md`

## README 清单

- `README.md`
- `atlas-gateway/README.md`
- `atlas-auth/README.md`
- `atlas-service/atlas-system/README.md`
- `atlas-service-api/atlas-system-api/README.md`
- `atlas-common/atlas-common-infra/atlas-common-infra-web/README.md`
- `atlas-common/atlas-common-infra/atlas-common-infra-redis/README.md`
- `atlas-common/atlas-common-infra/atlas-common-infra-db/README.md`
- `atlas-common/atlas-common-infra/atlas-common-infra-logging/README.md`
- `atlas-common/atlas-common-feature/atlas-common-feature-core/README.md`
- `atlas-common/atlas-common-feature/atlas-common-feature-security/README.md`
- `atlas-test-module/README.md`

> 排除范围：`specs/**/contracts/README.md` 与 `atlas-service/atlas-system/sql/**/README.md`（规范文档/SQL 说明，不属于模块架构说明）

## 验证

- 核对结果覆盖所有模块 README（清单内 12 份）
- 抽样复核不一致项并确认可复现（抽样见下）

## 抽样复核

- 抽样 3 份 README：`README.md`、`atlas-gateway/README.md`、`atlas-service-api/atlas-system-api/README.md`
- 复核结论：技术栈版本与宪法不一致项可复现；API 模块包结构描述不一致项可复现
