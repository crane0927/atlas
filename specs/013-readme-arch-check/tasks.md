# 任务清单

## 宪法合规检查清单

- [x] 技术栈版本符合宪法要求（JDK 21, Spring Boot 3.5.9, Spring Cloud 2025.0.1）
- [x] API 设计遵循 RESTful 规范（本功能无接口变更）
- [x] 代码注释使用中文
- [x] 已识别可复用的公共方法
- [x] 模块化归属正确（文档核对符合模块职责边界）

## 依赖顺序

- US1 → US2 → Polish

## Phase 1: Setup

- [x] T001 汇总 README 核对清单 `specs/013-readme-arch-check/quickstart.md`

## Phase 2: Foundational

- [x] T002 记录核对基准与判断规则 `specs/013-readme-arch-check/research.md`

## Phase 3: US1（README 一致性核对）

**目标**: 覆盖所有模块 README 并记录一致/不一致结论。

**独立验收**:
- README 清单覆盖所有模块
- 核对结果有结论与差异点记录

- [x] T003 [US1] 列出全部 README 清单 `specs/013-readme-arch-check/quickstart.md`
- [x] T004 [US1] 建立核对结果清单 `specs/013-readme-arch-check/README-audit.md`
- [x] T005 [US1] 逐项核对 README 与架构规范一致性并记录 `specs/013-readme-arch-check/README-audit.md`

## Phase 4: US2（结果汇总与复核）

**目标**: 输出可追踪核对结果清单并完成抽样复核。

**独立验收**:
- 结果清单可追踪
- 抽样复核可复现

- [x] T006 [US2] 汇总不一致项与差异说明 `specs/013-readme-arch-check/README-audit.md`
- [x] T007 [US2] 记录抽样复核结果 `specs/013-readme-arch-check/quickstart.md`

## Phase 5: Polish & Cross-cutting

- [x] T008 更新快速开始与验证说明 `specs/013-readme-arch-check/quickstart.md`

## 并行执行示例

- US1: T003 与 T004 可并行（不同输出文件）

## 实施策略

- 先补齐 README 清单与核对基准（T001-T002）
- 再完成核对与结果汇总（T003-T006）
- 最后补充复核与使用说明（T007-T008）
