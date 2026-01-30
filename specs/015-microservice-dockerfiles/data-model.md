# 数据模型：微服务 Dockerfile 创建

## 概述

本功能为基础设施配置功能，不涉及数据库实体或数据模型。

## 配置文件结构

### Dockerfile 配置模型

```
docker/
├── Dockerfile.build    # 编译阶段 Dockerfile
└── Dockerfile.run      # 运行阶段 Dockerfile
```

### 配置项说明

#### Dockerfile.build 配置项

| 配置项 | 类型 | 说明 |
|--------|------|------|
| BASE_IMAGE | String | 构建基础镜像（maven:3.9-eclipse-temurin-21） |
| WORKDIR | String | 工作目录（/app） |
| BUILD_CMD | String | 构建命令（mvn clean package -DskipTests） |

#### Dockerfile.run 配置项

| 配置项 | 类型 | 说明 |
|--------|------|------|
| BASE_IMAGE | String | 运行基础镜像（eclipse-temurin:21-jre） |
| APP_USER | String | 运行用户（atlas） |
| APP_PORT | Integer | 服务端口 |
| JVM_OPTS | String | JVM 参数 |
| HEALTHCHECK | Object | 健康检查配置 |

### 环境变量模型

各服务运行时需要的环境变量：

| 变量名 | 服务 | 说明 | 默认值 |
|--------|------|------|--------|
| SERVER_PORT | 所有服务 | 服务端口 | 各服务不同 |
| SPRING_PROFILES_ACTIVE | 所有服务 | 激活的配置文件 | dev |
| NACOS_SERVER_ADDR | 所有服务 | Nacos 服务地址 | localhost:8848 |
| NACOS_NAMESPACE | 所有服务 | Nacos 命名空间 | dev |
| JAVA_OPTS | 所有服务 | JVM 参数 | -Xms256m -Xmx512m |

## 实体关系

不适用 - 本功能不涉及数据库实体。

## 状态转换

不适用 - 本功能不涉及状态管理。
