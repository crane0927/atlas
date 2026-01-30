# 任务清单：微服务 Dockerfile 创建

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [x] 技术栈版本符合宪法要求（JDK 21, Spring Boot 3.5.9）
- [x] 代码注释使用中文
- [x] 可启动微服务包含 docker 目录（Dockerfile.build 和 Dockerfile.run）

## 功能概述

为 Atlas 项目的 3 个可启动微服务创建标准化的 Docker 容器配置。

| 服务 | 模块路径 | 端口 |
|------|----------|------|
| atlas-gateway | /atlas-gateway | 8080 |
| atlas-auth | /atlas-auth | 8084 |
| atlas-system | /atlas-service/atlas-system | 8085 |

## 用户场景映射

| 用户场景 | 描述 | 优先级 |
|----------|------|--------|
| US1 | atlas-gateway Dockerfile 创建 | P1 |
| US2 | atlas-auth Dockerfile 创建 | P1 |
| US3 | atlas-system Dockerfile 创建 | P1 |

---

## Phase 1: Setup（项目初始化）

**目标**: 创建各服务的 docker 目录结构

- [x] T001 [P] 创建 atlas-gateway/docker/ 目录
- [x] T002 [P] 创建 atlas-auth/docker/ 目录
- [x] T003 [P] 创建 atlas-service/atlas-system/docker/ 目录

**并行执行**: T001、T002、T003 可同时执行

---

## Phase 2: Foundational（基础配置）

**目标**: 无前置阻塞任务，各服务 Dockerfile 相互独立

此阶段无任务，直接进入用户场景实施。

---

## Phase 3: US1 - atlas-gateway Dockerfile

**用户场景**: 为 API 网关服务创建 Docker 容器配置

**独立测试标准**:
- atlas-gateway 可独立构建镜像
- 容器启动后 8080 端口可访问
- 健康检查通过

### 实施任务

- [x] T004 [US1] 创建 atlas-gateway 编译阶段 Dockerfile 在 atlas-gateway/docker/Dockerfile.build
- [x] T005 [US1] 创建 atlas-gateway 运行阶段 Dockerfile 在 atlas-gateway/docker/Dockerfile.run

**任务详情**:

#### T004: Dockerfile.build (atlas-gateway)

文件路径: `atlas-gateway/docker/Dockerfile.build`

配置要点:
- 基础镜像: `maven:3.9-eclipse-temurin-21`
- 工作目录: `/build`
- 构建命令: `mvn clean package -DskipTests -B`
- 中文注释说明各步骤用途

#### T005: Dockerfile.run (atlas-gateway)

文件路径: `atlas-gateway/docker/Dockerfile.run`

配置要点:
- 基础镜像: `eclipse-temurin:21-jre`
- 暴露端口: 8080
- 非 root 用户: atlas (uid=1001)
- 健康检查: `/actuator/health`
- JVM 参数: 容器感知配置

---

## Phase 4: US2 - atlas-auth Dockerfile

**用户场景**: 为认证授权服务创建 Docker 容器配置

**独立测试标准**:
- atlas-auth 可独立构建镜像
- 容器启动后 8084 端口可访问
- 健康检查通过

### 实施任务

- [x] T006 [P] [US2] 创建 atlas-auth 编译阶段 Dockerfile 在 atlas-auth/docker/Dockerfile.build
- [x] T007 [P] [US2] 创建 atlas-auth 运行阶段 Dockerfile 在 atlas-auth/docker/Dockerfile.run

**任务详情**:

#### T006: Dockerfile.build (atlas-auth)

文件路径: `atlas-auth/docker/Dockerfile.build`

配置要点:
- 基础镜像: `maven:3.9-eclipse-temurin-21`
- 工作目录: `/build`
- 构建命令: `mvn clean package -DskipTests -B`
- 中文注释说明各步骤用途

#### T007: Dockerfile.run (atlas-auth)

文件路径: `atlas-auth/docker/Dockerfile.run`

配置要点:
- 基础镜像: `eclipse-temurin:21-jre`
- 暴露端口: 8084
- 非 root 用户: atlas (uid=1001)
- 健康检查: `/actuator/health`
- JVM 参数: 容器感知配置

---

## Phase 5: US3 - atlas-system Dockerfile

**用户场景**: 为系统管理服务创建 Docker 容器配置

**独立测试标准**:
- atlas-system 可独立构建镜像
- 容器启动后 8085 端口可访问
- 健康检查通过

### 实施任务

- [x] T008 [P] [US3] 创建 atlas-system 编译阶段 Dockerfile 在 atlas-service/atlas-system/docker/Dockerfile.build
- [x] T009 [P] [US3] 创建 atlas-system 运行阶段 Dockerfile 在 atlas-service/atlas-system/docker/Dockerfile.run

**任务详情**:

#### T008: Dockerfile.build (atlas-system)

文件路径: `atlas-service/atlas-system/docker/Dockerfile.build`

配置要点:
- 基础镜像: `maven:3.9-eclipse-temurin-21`
- 工作目录: `/build`
- 构建命令: `mvn clean package -DskipTests -B`
- 中文注释说明各步骤用途

#### T009: Dockerfile.run (atlas-system)

文件路径: `atlas-service/atlas-system/docker/Dockerfile.run`

配置要点:
- 基础镜像: `eclipse-temurin:21-jre`
- 暴露端口: 8085
- 非 root 用户: atlas (uid=1001)
- 健康检查: `/actuator/health`
- JVM 参数: 容器感知配置

---

## Phase 6: Polish（验证与收尾）

**目标**: 验证所有 Dockerfile 符合宪法规范

### 验证任务

- [x] T010 验证所有 Dockerfile 包含中文注释
- [x] T011 验证目录结构符合宪法原则 17 规范
- [x] T012 更新各服务 README.md 添加 Docker 使用说明

---

## 依赖关系图

```
Phase 1 (Setup)
    T001 ─┬─→ T004 ─→ T005 (US1: atlas-gateway)
    T002 ─┼─→ T006 ─→ T007 (US2: atlas-auth)  
    T003 ─┴─→ T008 ─→ T009 (US3: atlas-system)
                              ↓
                         Phase 6 (Polish)
                         T010, T011, T012
```

**说明**:
- Phase 1 的目录创建任务可并行执行
- US1、US2、US3 三个用户场景相互独立，可并行实施
- Phase 6 需等待所有用户场景完成

---

## 并行执行示例

### 最大并行度执行

```bash
# 第一批（并行）
T001, T002, T003  # 创建目录

# 第二批（并行）- 所有 Dockerfile 可同时创建
T004, T005, T006, T007, T008, T009

# 第三批（顺序）- 验证
T010 → T011 → T012
```

### 按用户场景分配执行

```bash
# 开发者 A: US1 (atlas-gateway)
T001 → T004 → T005

# 开发者 B: US2 (atlas-auth)
T002 → T006 → T007

# 开发者 C: US3 (atlas-system)
T003 → T008 → T009

# 合并后: 验证
T010 → T011 → T012
```

---

## 实施策略

### MVP 范围

**建议 MVP**: 仅完成 US1 (atlas-gateway)

- 任务: T001, T004, T005
- 交付物: atlas-gateway 的完整 Docker 配置
- 验证: 可构建镜像并运行容器

### 增量交付

| 迭代 | 用户场景 | 交付物 |
|------|----------|--------|
| 迭代 1 | US1 | atlas-gateway Dockerfile |
| 迭代 2 | US2 | atlas-auth Dockerfile |
| 迭代 3 | US3 | atlas-system Dockerfile |
| 迭代 4 | Polish | 验证 + 文档更新 |

---

## 任务统计

| 指标 | 数值 |
|------|------|
| 总任务数 | 12 |
| Setup 任务 | 3 |
| US1 任务 | 2 |
| US2 任务 | 2 |
| US3 任务 | 2 |
| Polish 任务 | 3 |
| 可并行任务 | 9 (T001-T009) |

---

## 验收清单

- [x] atlas-gateway 包含 `docker/Dockerfile.build` 和 `docker/Dockerfile.run`
- [x] atlas-auth 包含 `docker/Dockerfile.build` 和 `docker/Dockerfile.run`
- [x] atlas-system 包含 `docker/Dockerfile.build` 和 `docker/Dockerfile.run`
- [x] 所有 Dockerfile 包含中文注释
- [x] 所有运行阶段 Dockerfile 配置了端口暴露
- [x] 所有运行阶段 Dockerfile 配置了健康检查
- [x] 目录结构和文件命名符合宪法规范

---

## 相关文档

- [功能规格说明](./spec.md)
- [实施计划](./plan.md)
- [研究文档](./research.md)
- [快速开始](./quickstart.md)
