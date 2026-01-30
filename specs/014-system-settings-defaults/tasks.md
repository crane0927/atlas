# 任务清单（系统默认设置）

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [ ] 技术栈版本符合宪法要求（JDK 21, Spring Boot 3.5.9, Spring Cloud 2025.0.1, Spring Cloud Alibaba 2025.0.0.0）
- [ ] API 设计遵循 RESTful 规范
- [ ] 代码注释使用中文
- [ ] 已识别可复用的公共方法

## 任务概览

**总任务数**: 27  
**阶段数**: 7  
**可并行任务**: 10  
**用户故事数**: 4

## 依赖关系

### 阶段完成顺序

1. **Phase 1: Setup** → 必须先完成
2. **Phase 2: Foundational** → 依赖 Phase 1
3. **Phase 3: US1 - 列表查询与筛选** → 依赖 Phase 2
4. **Phase 4: US2 - 系统类型值修改** → 依赖 Phase 2
5. **Phase 5: US3 - 新增自定义设置** → 依赖 Phase 2
6. **Phase 6: US4 - 删除自定义设置** → 依赖 Phase 2
7. **Phase 7: Polish & Cross-cutting** → 依赖 Phase 3-6

### 用户故事完成顺序

US1 → US2 → US3 → US4

## 实施策略

**MVP 范围**: Phase 2 + Phase 3（数据模型与列表查询）  
**增量交付**:
1. 先完成数据模型与迁移脚本
2. 再完成查询与编辑/新增/删除接口
3. 最后补齐测试与文档

## Phase 1: Setup

**目标**: 固化规范与复用点  
**独立测试标准**: 迁移规范与异常处理复用点确认完毕

- [ ] T001 确认 Flyway 迁移命名规范（参考 `atlas-service/atlas-system/sql/v1.0.0/README.md`）
- [ ] T002 确认统一异常处理复用点（参考 `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`）
- [ ] T003 记录权限校验复用点到 `specs/014-system-settings-defaults/research.md`

## Phase 2: Foundational

**目标**: 完成数据结构与持久层基础  
**独立测试标准**: 表结构与实体定义完成

- [ ] T004 创建迁移脚本 `atlas-service/atlas-system/sql/v1.0.0/006_create_system_setting_table.sql`
- [ ] T005 [P] 新增枚举 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/model/enums/SystemSettingType.java`
- [ ] T006 [P] 新增实体 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/model/entity/SystemSetting.java`
- [ ] T007 [P] 新增 Mapper `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/mapper/SystemSettingMapper.java`
- [ ] T008 [P] 新增 Service 接口 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/service/SystemSettingService.java`
- [ ] T009 [P] 新增 Service 实现 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/service/impl/SystemSettingServiceImpl.java`

## Phase 3: US1 - 列表查询与筛选

**目标**: 管理员可查看设置项列表并按类型/关键字筛选  
**独立测试标准**: 列表接口返回正确且区分类型  
**用户故事**: 列表查询与筛选

- [ ] T010 [P] [US1] 新增查询 DTO `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/model/dto/SystemSettingQueryDTO.java`
- [ ] T011 [P] [US1] 新增 VO `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/model/vo/SystemSettingVO.java`
- [ ] T012 [US1] 实现查询逻辑于 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/service/impl/SystemSettingServiceImpl.java`
- [ ] T013 [US1] 暴露 GET 接口于 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/controller/SystemSettingController.java`

## Phase 4: US2 - 系统类型值修改

**目标**: 系统类型仅允许修改 value  
**独立测试标准**: 修改系统类型仅更新 value，其他字段不可变  
**用户故事**: 系统类型值修改

- [ ] T014 [P] [US2] 新增更新 DTO `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/model/dto/SystemSettingUpdateDTO.java`
- [ ] T015 [US2] 实现更新逻辑（限制 SYSTEM 类型仅改 value）于 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/service/impl/SystemSettingServiceImpl.java`
- [ ] T016 [US2] 暴露 PUT 接口于 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/controller/SystemSettingController.java`
- [ ] T017 [US2] 增补错误码于 `atlas-service/atlas-system/src/main/java/com/atlas/system/constant/SystemErrorCode.java`

## Phase 5: US3 - 新增自定义设置

**目标**: 支持新增 CUSTOM 类型设置项  
**独立测试标准**: key 唯一性校验生效且新增成功  
**用户故事**: 新增自定义设置

- [ ] T018 [P] [US3] 新增创建 DTO `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/model/dto/SystemSettingCreateDTO.java`
- [ ] T019 [US3] 实现创建逻辑（仅允许 CUSTOM、校验 key 唯一）于 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/service/impl/SystemSettingServiceImpl.java`
- [ ] T020 [US3] 暴露 POST 接口于 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/controller/SystemSettingController.java`

## Phase 6: US4 - 删除自定义设置

**目标**: 支持删除 CUSTOM，拒绝 SYSTEM 删除  
**独立测试标准**: SYSTEM 删除被拦截且返回明确错误  
**用户故事**: 删除自定义设置

- [ ] T021 [US4] 实现删除逻辑（SYSTEM 拒绝删除）于 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/service/impl/SystemSettingServiceImpl.java`
- [ ] T022 [US4] 暴露 DELETE 接口于 `atlas-service/atlas-system/src/main/java/com/atlas/system/settings/controller/SystemSettingController.java`

## Phase 7: Polish & Cross-cutting

**目标**: 覆盖核心流程并完善文档  
**独立测试标准**: 关键规则测试通过且文档可验证

- [ ] T023 [P] 编写 Service 单测 `atlas-service/atlas-system/src/test/java/com/atlas/system/settings/service/SystemSettingServiceTest.java`
- [ ] T024 [P] 编写 Controller 测试 `atlas-service/atlas-system/src/test/java/com/atlas/system/settings/controller/SystemSettingControllerTest.java`
- [ ] T025 [P] 编写 API 测试 `atlas-service/atlas-system/src/test/java/com/atlas/system/settings/controller/SystemSettingApiTest.java`
- [ ] T026 更新接口合约 `specs/014-system-settings-defaults/contracts/README.md`
- [ ] T027 更新快速开始 `specs/014-system-settings-defaults/quickstart.md`

## 并行执行示例

### US1（列表查询与筛选）并行执行

```bash
# 可并行完成 DTO/VO 定义
T010 & T011
```

### US3（新增自定义设置）并行执行

```bash
# 可并行完成 DTO 与 Controller 骨架
T018 & T020
```

### Phase 2（Foundational）并行执行

```bash
# 可并行完成枚举/实体/Mapper/Service 骨架
T005 & T006 & T007 & T008 & T009
```
