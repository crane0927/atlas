# 功能规划文档：微服务 Dockerfile 创建

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 3.5.9, Spring Cloud 2025.0.1, Spring Cloud Alibaba 2025.0.0.0
- ✅ **中文注释**: 所有配置文件使用中文注释
- ✅ **模块化**: 遵循分层架构
- ✅ **Dockerfile**: 可启动微服务需创建 docker 目录，包含 Dockerfile.build 和 Dockerfile.run

## 功能概述

为 Atlas 项目的 3 个可启动微服务创建标准化的 Docker 容器配置，包括编译阶段和运行阶段的 Dockerfile。

### 业务价值

1. **环境一致性**: 开发、测试、生产环境保持一致
2. **部署标准化**: 统一的容器化方案便于运维管理
3. **可扩展性**: 容器化服务便于水平扩展
4. **隔离性**: 每个服务独立运行，互不影响

## 技术方案

### 架构设计

```
atlas/
├── atlas-gateway/
│   └── docker/
│       ├── Dockerfile.build    # 编译阶段
│       └── Dockerfile.run      # 运行阶段
├── atlas-auth/
│   └── docker/
│       ├── Dockerfile.build
│       └── Dockerfile.run
└── atlas-service/
    └── atlas-system/
        └── docker/
            ├── Dockerfile.build
            └── Dockerfile.run
```

### 技术选型

| 组件 | 选型 | 版本 | 说明 |
|------|------|------|------|
| JDK 构建镜像 | maven:3.9-eclipse-temurin-21 | 3.9.x | 包含 Maven 和 JDK 21 |
| JDK 运行镜像 | eclipse-temurin:21-jre | 21 | 轻量级 JRE 运行时 |
| 健康检查 | Spring Boot Actuator | - | /actuator/health |

### 服务端口配置

| 服务 | 模块路径 | 端口 |
|------|----------|------|
| atlas-gateway | /atlas-gateway | 8080 |
| atlas-auth | /atlas-auth | 8084 |
| atlas-system | /atlas-service/atlas-system | 8085 |

## 实施计划

### 阶段 1: 创建 Dockerfile 模板

**目标**: 创建标准化的 Dockerfile 模板

**任务**:
1. 设计 Dockerfile.build 模板结构
2. 设计 Dockerfile.run 模板结构
3. 定义 JVM 参数配置
4. 定义健康检查配置

**交付物**:
- Dockerfile 模板设计文档

### 阶段 2: 为各服务创建 Dockerfile

**目标**: 为 3 个微服务创建 Dockerfile

**任务**:

| 任务 | 服务 | 文件 |
|------|------|------|
| 2.1 | atlas-gateway | docker/Dockerfile.build |
| 2.2 | atlas-gateway | docker/Dockerfile.run |
| 2.3 | atlas-auth | docker/Dockerfile.build |
| 2.4 | atlas-auth | docker/Dockerfile.run |
| 2.5 | atlas-system | docker/Dockerfile.build |
| 2.6 | atlas-system | docker/Dockerfile.run |

**交付物**:
- 6 个 Dockerfile 文件（3 服务 × 2 阶段）

### 阶段 3: 验证与测试

**目标**: 验证 Dockerfile 正确性

**任务**:
1. 本地构建镜像测试
2. 容器启动测试
3. 健康检查验证
4. 中文注释完整性检查

**交付物**:
- 验证报告

## Dockerfile 设计

### Dockerfile.build 模板

```dockerfile
# ============================================================
# 编译阶段 Dockerfile
# 用途: 构建 Spring Boot 应用程序
# 基础镜像: maven:3.9-eclipse-temurin-21
# ============================================================

FROM maven:3.9-eclipse-temurin-21 AS builder

# 设置工作目录
WORKDIR /build

# 复制 Maven 配置文件（利用 Docker 层缓存）
COPY pom.xml .
COPY ../pom.xml ../pom.xml

# 下载依赖（独立层，加速后续构建）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用程序
RUN mvn clean package -DskipTests -B

# 输出: /build/target/*.jar
```

### Dockerfile.run 模板

```dockerfile
# ============================================================
# 运行阶段 Dockerfile
# 用途: 运行 Spring Boot 应用程序
# 基础镜像: eclipse-temurin:21-jre
# ============================================================

FROM eclipse-temurin:21-jre

# 设置标签
LABEL maintainer="Atlas Team"
LABEL description="Atlas 微服务"

# 创建非特权用户
RUN addgroup --system --gid 1001 atlas && \
    adduser --system --uid 1001 --gid 1001 atlas

# 设置工作目录
WORKDIR /app

# 复制应用程序 JAR
COPY target/*.jar app.jar

# 设置文件权限
RUN chown -R atlas:atlas /app

# 切换到非特权用户
USER atlas

# 暴露端口
EXPOSE ${PORT}

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

# JVM 参数配置
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

## 风险评估

| 风险 | 影响 | 概率 | 应对措施 |
|------|------|------|----------|
| Actuator 未启用 | 健康检查失败 | 中 | 检查各服务是否有 Actuator 依赖 |
| 端口配置不一致 | 容器无法访问 | 低 | 使用环境变量配置端口 |
| 镜像体积过大 | 部署效率降低 | 低 | 使用 JRE 镜像，优化层结构 |
| 多模块构建问题 | 编译失败 | 中 | 需要处理父 POM 依赖 |

## 验收标准

| 编号 | 标准 | 验证方法 |
|------|------|----------|
| AC-1 | 所有服务有 docker/ 目录 | 检查目录结构 |
| AC-2 | 包含 Dockerfile.build 和 Dockerfile.run | 检查文件存在 |
| AC-3 | Dockerfile 包含中文注释 | 代码审查 |
| AC-4 | 容器可正常启动 | docker run 测试 |
| AC-5 | 健康检查通过 | curl /actuator/health |
| AC-6 | 使用非 root 用户运行 | docker inspect |

## 相关文档

- [功能规格说明](./spec.md)
- [研究文档](./research.md)
- [数据模型](./data-model.md)
- [快速开始](./quickstart.md)
