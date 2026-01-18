# README 架构核对结果

## 核对范围

覆盖项目模块 README（共 12 份）。排除 `specs/**/contracts/README.md` 与 `atlas-service/atlas-system/sql/**/README.md`。

## 核对结果清单

| README | 结论 | 说明 |
| --- | --- | --- |
| `README.md` | 不一致 | 技术栈版本与宪法不一致（Spring Boot 4.0.1 / Spring Cloud 2025.1.0 / Spring Cloud Alibaba 2025.1.0） |
| `atlas-gateway/README.md` | 不一致 | 前置条件中的技术栈版本与宪法不一致（Spring Boot 4.0.1 / Spring Cloud 2025.1.0 / Spring Cloud Alibaba 2025.1.0） |
| `atlas-auth/README.md` | 一致 | 模块职责与依赖描述符合宪法与模块边界 |
| `atlas-service/atlas-system/README.md` | 部分一致 | 端口与数据库示例配置与当前默认配置不一致（文档 8082 / atlas_system；实际 8085 / atlas schema） |
| `atlas-service-api/atlas-system-api/README.md` | 部分一致 | 文档说明“仅定义接口契约和 DTO”，但包结构示例包含 `vo/` 预留 |
| `atlas-common/atlas-common-infra-web/README.md` | 一致 | Web 基础设施职责描述与模块边界一致 |
| `atlas-common/atlas-common-infra-redis/README.md` | 一致 | Redis 基础设施职责描述与模块边界一致 |
| `atlas-common/atlas-common-infra-db/README.md` | 一致 | DB 基础设施职责描述与模块边界一致 |
| `atlas-common/atlas-common-infra-logging/README.md` | 一致 | 日志基础设施职责描述与模块边界一致 |
| `atlas-common/atlas-common-feature/atlas-common-feature-core/README.md` | 一致 | 核心特性职责描述与模块边界一致 |
| `atlas-common/atlas-common-feature/atlas-common-feature-security/README.md` | 一致 | 安全特性职责描述与模块边界一致 |
| `atlas-test-module/README.md` | 不一致 | 技术栈版本与宪法不一致（Spring Boot 4.0.1） |

## 不一致项汇总

- 技术栈版本不一致：`README.md`、`atlas-gateway/README.md`、`atlas-test-module/README.md`
- 配置示例与当前默认配置不一致：`atlas-service/atlas-system/README.md`
- API 模块职责描述与包结构示例不一致：`atlas-service-api/atlas-system-api/README.md`
