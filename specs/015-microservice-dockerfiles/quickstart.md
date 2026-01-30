# 快速开始：微服务 Dockerfile 使用指南

## 概述

本文档说明如何使用 Atlas 项目的 Dockerfile 构建和运行微服务容器。

## 前置条件

- Docker 20.10+ 或兼容的容器运行时
- 项目源代码已克隆到本地
- Maven 3.9+（用于本地构建）

## 目录结构

每个可启动微服务的 `docker/` 目录包含：

```
{service-module}/
├── docker/
│   ├── Dockerfile.build    # 编译阶段 Dockerfile
│   └── Dockerfile.run      # 运行阶段 Dockerfile
├── src/
└── pom.xml
```

## 构建流程

### 方式一：两阶段构建（推荐用于 CI/CD）

#### 1. 编译阶段

使用 `Dockerfile.build` 构建应用：

```bash
# 进入服务模块目录
cd atlas-gateway

# 构建编译镜像并导出 JAR
docker build -f docker/Dockerfile.build -t atlas-gateway-build .

# 从构建容器中复制 JAR 文件
docker create --name temp-build atlas-gateway-build
docker cp temp-build:/app/target/*.jar ./target/
docker rm temp-build
```

#### 2. 运行阶段

使用 `Dockerfile.run` 创建运行镜像：

```bash
# 构建运行镜像
docker build -f docker/Dockerfile.run -t atlas-gateway:latest .
```

### 方式二：本地构建后打包（推荐用于本地开发）

```bash
# 1. 本地 Maven 构建
mvn clean package -DskipTests

# 2. 构建运行镜像
docker build -f docker/Dockerfile.run -t atlas-gateway:latest .
```

## 运行容器

### 基本运行

```bash
# 运行 atlas-gateway
docker run -d \
  --name atlas-gateway \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e NACOS_SERVER_ADDR=host.docker.internal:8848 \
  atlas-gateway:latest

# 运行 atlas-auth
docker run -d \
  --name atlas-auth \
  -p 8084:8084 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e NACOS_SERVER_ADDR=host.docker.internal:8848 \
  atlas-auth:latest

# 运行 atlas-system
docker run -d \
  --name atlas-system \
  -p 8085:8085 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e NACOS_SERVER_ADDR=host.docker.internal:8848 \
  atlas-system:latest
```

### 自定义 JVM 参数

```bash
docker run -d \
  --name atlas-gateway \
  -p 8080:8080 \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  atlas-gateway:latest
```

### 挂载配置文件

```bash
docker run -d \
  --name atlas-gateway \
  -p 8080:8080 \
  -v /path/to/config:/app/config:ro \
  atlas-gateway:latest
```

## 健康检查

### 手动检查

```bash
# 检查服务健康状态
curl http://localhost:8080/actuator/health
```

### Docker 健康检查

容器内置健康检查，可通过以下命令查看：

```bash
docker inspect --format='{{.State.Health.Status}}' atlas-gateway
```

## 查看日志

```bash
# 查看实时日志
docker logs -f atlas-gateway

# 查看最近 100 行日志
docker logs --tail 100 atlas-gateway
```

## 停止和删除容器

```bash
# 停止容器
docker stop atlas-gateway

# 删除容器
docker rm atlas-gateway

# 删除镜像
docker rmi atlas-gateway:latest
```

## 服务端口对照表

| 服务 | 默认端口 | 健康检查端点 |
|------|----------|--------------|
| atlas-gateway | 8080 | /actuator/health |
| atlas-auth | 8084 | /actuator/health |
| atlas-system | 8085 | /actuator/health |

## 常见问题

### Q: 容器启动后无法连接 Nacos

**A**: 检查 `NACOS_SERVER_ADDR` 环境变量配置。在 Docker 环境中，使用 `host.docker.internal` 访问宿主机服务。

### Q: 容器内存占用过高

**A**: 通过 `JAVA_OPTS` 环境变量调整 JVM 堆内存配置：
```bash
-e JAVA_OPTS="-Xms256m -Xmx512m"
```

### Q: 健康检查失败

**A**: 检查以下几点：
1. 服务是否正常启动（查看日志）
2. Actuator 端点是否启用
3. 端口映射是否正确

## 下一步

- 配置 CI/CD 流水线自动化构建
- 使用 Docker Compose 编排多服务部署
- 配置 Kubernetes 部署清单
