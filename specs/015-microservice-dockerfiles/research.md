# 研究文档：微服务 Dockerfile 创建

## 研究目标

为 Atlas 项目的可启动微服务选择合适的 Docker 基础镜像和配置方案。

## 技术决策

### 1. JDK 21 Docker 基础镜像选择

**决策**: 使用 Eclipse Temurin JDK 21 官方镜像

**理由**:
- Eclipse Temurin 是 Eclipse Adoptium 项目提供的 OpenJDK 发行版
- 完全开源，免费使用，无许可证限制
- 官方提供多种基础镜像变体（alpine、slim、full）
- 社区活跃，更新及时，安全补丁发布快
- 与 OpenJDK 100% 兼容

**备选方案**:
| 镜像 | 优点 | 缺点 |
|------|------|------|
| `eclipse-temurin:21` | 官方推荐，稳定可靠 | 镜像体积较大 |
| `eclipse-temurin:21-alpine` | 体积小（约 200MB） | Alpine 兼容性问题 |
| `amazoncorretto:21` | AWS 优化 | 绑定 AWS 生态 |
| `azul/zulu-openjdk:21` | 性能优化 | 部分功能需商业授权 |

**最终选择**:
- 编译阶段: `eclipse-temurin:21-jdk` (需要完整 JDK 工具链)
- 运行阶段: `eclipse-temurin:21-jre` (仅需 JRE，体积更小)

### 2. Maven 构建镜像选择

**决策**: 使用 `maven:3.9-eclipse-temurin-21` 官方镜像

**理由**:
- 官方维护，包含 Maven 3.9.x 最新版本
- 使用与运行阶段相同的 JDK 发行版（Eclipse Temurin）
- 支持多阶段构建的构建缓存优化

### 3. 服务端口配置

根据项目配置文件确认各服务端口：

| 服务名称 | 配置文件端口 | 用途 |
|----------|-------------|------|
| atlas-gateway | 8080 | API 网关入口 |
| atlas-auth | 8084 | 认证授权服务 |
| atlas-system | 8085 | 系统管理服务（默认） |

### 4. 健康检查方案

**决策**: 使用 Spring Boot Actuator 健康检查端点

**理由**:
- Spring Boot 内置 Actuator 提供标准化健康检查
- 支持 `/actuator/health` 端点
- 可配置详细的健康状态信息

**健康检查配置**:
```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT}/actuator/health || exit 1
```

**注意**: 需要在各服务中启用 Actuator 依赖和配置。

### 5. JVM 参数配置

**决策**: 使用容器感知的 JVM 参数配置

**推荐 JVM 参数**:
```
-XX:+UseContainerSupport
-XX:MaxRAMPercentage=75.0
-XX:InitialRAMPercentage=50.0
-Djava.security.egd=file:/dev/./urandom
```

**参数说明**:
- `UseContainerSupport`: 自动识别容器资源限制（JDK 10+ 默认启用）
- `MaxRAMPercentage`: 最大堆内存占容器内存的百分比
- `InitialRAMPercentage`: 初始堆内存占容器内存的百分比
- `java.security.egd`: 加速随机数生成，减少启动时间

### 6. 安全配置

**决策**: 使用非 root 用户运行容器

**实现方式**:
```dockerfile
# 创建非特权用户
RUN addgroup --system --gid 1001 atlas && \
    adduser --system --uid 1001 --gid 1001 atlas

# 切换到非特权用户
USER atlas
```

**安全最佳实践**:
- 不在镜像中包含敏感信息
- 使用环境变量传递配置
- 禁止在容器中以 root 用户运行

### 7. 镜像层优化

**决策**: 利用 Docker 层缓存优化构建速度

**优化策略**:
1. 先复制依赖定义文件（pom.xml），再复制源代码
2. 分离依赖下载和代码编译步骤
3. 使用 `.dockerignore` 排除不必要的文件

## 服务模块信息

| 服务 | Artifact ID | 路径 | 端口 |
|------|-------------|------|------|
| API 网关 | atlas-gateway | `/atlas-gateway` | 8080 |
| 认证服务 | atlas-auth | `/atlas-auth` | 8084 |
| 系统服务 | atlas-system | `/atlas-service/atlas-system` | 8085 |

## 依赖关系

Dockerfile 创建需要以下先决条件：
- 各服务的 `pom.xml` 正确配置 Spring Boot Maven Plugin
- Spring Boot Actuator 依赖（用于健康检查）
- 服务端口配置在 `application.yml` 中明确定义

## 待确认事项

1. ~~JDK 基础镜像选择~~ → 已确认使用 Eclipse Temurin
2. ~~健康检查方案~~ → 已确认使用 Actuator
3. ~~非 root 用户配置~~ → 已确认实现方案

## 结论

所有技术决策已完成，无需进一步澄清。可以进入 Phase 1 设计阶段。
