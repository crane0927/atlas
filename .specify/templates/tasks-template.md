# 任务清单

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [ ] 技术栈版本符合宪法要求（JDK 21, Spring Boot 3.5.9, Spring Cloud 2025.0.1, Spring Cloud Alibaba 2025.0.0.0）
- [ ] 数据库使用 PostgreSQL + MyBatis-Plus
- [ ] API 设计遵循 RESTful 规范，统一使用 `Result<T>` 包装
- [ ] 代码注释使用中文
- [ ] 已识别可复用的公共方法
- [ ] Entity 继承 BaseEntity
- [ ] 可启动微服务包含 docker 目录（Dockerfile.build 和 Dockerfile.run）

## 开发任务

### 后端开发

- [ ] 创建数据模型（Entity）
- [ ] 创建数据传输对象（DTO）
- [ ] 实现 Service 层业务逻辑
- [ ] 实现 Controller 层 RESTful 接口
- [ ] 添加中文注释（类、方法、复杂逻辑）
- [ ] 提取公共方法，避免代码重复
- [ ] 实现统一异常处理

### 测试（仅在明确要求时）

- [ ] 编写单元测试（仅核心业务逻辑，覆盖率 ≥ 70%）
- [ ] 编写集成测试（按需）
- [ ] 编写 API 测试（按需）

> **注意**: AI 辅助开发时，非必要不生成单元测试。仅在用户明确要求或修改核心业务逻辑时编写。

### 文档

- [ ] 更新 API 文档
- [ ] 更新开发文档

### 容器化（如涉及新建可启动微服务）

- [ ] 创建 docker 目录
- [ ] 创建 Dockerfile.build（编译阶段）
- [ ] 创建 Dockerfile.run（运行阶段）
- [ ] 配置 JVM 参数和端口暴露

## 代码审查检查点

- [ ] RESTful 设计规范
- [ ] 中文注释完整性
- [ ] 代码复用情况
- [ ] 异常处理规范性
- [ ] Entity 继承 BaseEntity
- [ ] DTO/VO 放在 model 包下
- [ ] 可启动微服务包含 Dockerfile（如适用）
- [ ] 测试覆盖率（仅限明确要求的测试）
