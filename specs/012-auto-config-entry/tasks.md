# 任务清单

## 宪法合规检查清单

- [ ] 技术栈版本符合宪法要求（JDK 21, Spring Boot 3.5.9, Spring Cloud 2025.0.1）
- [ ] API 设计遵循 RESTful 规范（本功能无接口变更）
- [ ] 代码注释使用中文
- [ ] 已识别可复用的公共方法
- [ ] 模块化归属正确（公共配置归属 `atlas-common-infra-web`）

## 依赖顺序

- US1 → US2 → Polish

## Phase 1: Setup

- [ ] T001 更新自动配置入口文档说明 `atlas-common/atlas-common-infra/atlas-common-infra-web/README.md`

## Phase 2: Foundational

- [ ] T002 创建自动配置类 `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/WebExceptionAutoConfiguration.java`
- [ ] T003 [P] 添加自动配置入口元数据 `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- [ ] T004 [P] 在自动配置类中注册异常处理扫描（复用现有 `GlobalExceptionHandler` 包路径）

## Phase 3: US1（自动启用统一异常响应）

**目标**: 业务服务无需显式导入配置即可启用统一异常响应。

**独立验收**:
- 业务服务移除显式导入后可正常启动
- 触发业务异常返回统一 `Result` 格式

- [ ] T005 [US1] 移除服务侧显式导入配置 `atlas-service/atlas-system/src/main/java/com/atlas/system/SystemApplication.java`
- [ ] T006 [US1] 删除服务侧临时配置类 `atlas-service/atlas-system/src/main/java/com/atlas/system/config/WebExceptionHandlerConfig.java`
- [ ] T007 [US1] 确认公共模块依赖关系不变（如需调整，更新 `atlas-service/atlas-system/pom.xml`）
- [ ] T008 [US1] 添加单元测试或最小启动验证说明 `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/...`（若已有测试基座则复用）

## Phase 4: US2（公共模块不可用时降级）

**目标**: 公共模块缺失时服务仍可启动并回退到默认异常响应。

**独立验收**:
- 移除公共模块依赖后服务仍可启动
- 响应回退到默认异常处理链路

- [ ] T009 [US2] 在自动配置类中增加条件加载与降级说明 `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/WebExceptionAutoConfiguration.java`
- [ ] T010 [US2] 编写降级场景验证用例或说明 `specs/012-auto-config-entry/quickstart.md`

## Phase 5: Polish & Cross-cutting

- [ ] T011 更新公共模块文档与示例 `atlas-common/atlas-common-infra/atlas-common-infra-web/README.md`
- [ ] T012 回归验证现有接口响应格式（成功/失败）并记录结果 `specs/012-auto-config-entry/quickstart.md`

## 并行执行示例

- US1: T005 与 T006 可并行
- Foundational: T003 与 T004 可并行

## 实施策略

- 先完成自动配置入口与元数据（T002-T004）
- 再移除服务侧显式导入（T005-T006）
- 最后补齐降级说明与验证（T009-T012）
