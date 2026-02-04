# 快速开始：宪法原则 20/21 合规改造验证

## 目标

改造完成后，通过既有接口与代码审查验证：对象转换已使用 BeanUtils/MapStruct，参数与空值处理已使用 Assert/Optional，且对外行为无变化。

## 前置条件

- JDK 21，Maven 可用。
- atlas-system 可启动（如本地 `http://localhost:8085` 或经网关转发）。

## 验证步骤

### 1. 编译与启动

```bash
# 从仓库根目录
mvn clean compile -pl atlas-service/atlas-system -am

# 启动 atlas-system（若需本地验证）
# 使用 IDE 或: mvn spring-boot:run -pl atlas-service/atlas-system
```

### 2. 接口回归（行为不变）

对以下既有分页/查询接口做一次调用，确认响应结构与数据与改造前一致（可对比改造前响应或与预期一致即可）：

```bash
# 用户分页
curl -s "http://localhost:8085/api/v1/users?page=1&size=10"

# 角色分页
curl -s "http://localhost:8085/api/v1/roles?page=1&size=10"

# 权限分页
curl -s "http://localhost:8085/api/v1/permissions?page=1&size=10"

# 系统设置分页
curl -s "http://localhost:8085/api/v1/system-settings/page?page=1&size=10"
```

确认：响应为 `Result<PageResult<...>>`，包含 `list`、`total`、`page`、`size`、`pages`；默认排序与筛选行为符合预期。

### 3. 代码审查检查点

- **原则 20**：检索 `convertTo`、`toVO`、`toDTO` 及成段 `setXxx(getXxx())`，确认已改为 BeanUtils.copyProperties 或 MapStruct，无手写逐字段转换。
- **原则 21**：检索分页/查询方法中的 `query != null ?`、`x == null ?`，确认已改为 Assert 或 Optional，无冗长 if-else 判空链。

## 注意事项

- 本需求不新增单元测试；若已有相关测试，改造后应全部通过。
- 若发现某处改造后行为异常，应回滚该处并检查 copyProperties 的源/目标字段或 Optional/Assert 的语义是否与原先 if-else 一致。
