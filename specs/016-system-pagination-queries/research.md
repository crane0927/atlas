# 研究文档：通用分页 DTO 与 atlas-system 分页查询

## 研究目标

确定通用分页请求的落位、排序语义及与现有 SystemSetting 分页的对齐方式。

## 技术决策

### 1. 通用分页请求 DTO 放置位置

**决策**：在 `atlas-common-feature-core` 的 `page` 包下新增通用分页请求 DTO（如 `PageQueryDTO`）。

**理由**：
- 与现有 `PageResult` 同处 `com.atlas.common.feature.core.page`，请求/响应对称，便于复用。
- common-feature-core 已被 atlas-system 等模块依赖，无需新增依赖。
- 宪法约定 DTO 用于跨层传输与接口契约，通用分页请求属于跨模块契约。

**备选**：放在 atlas-system 内仅限 system 使用 → 不利于其他微服务复用，不采纳。

### 2. 分页请求字段与命名

**决策**：
- **page**：当前页码，从 1 开始，默认 1。
- **size**：每页条数，默认 10，建议设置合理上限（如 100）。
- **sort**：排序，格式采用单字符串（如 `createTime,desc` 或 `username,asc`），与 Spring Data / 常见 REST 习惯一致；可选，不传则使用默认排序。

**理由**：
- 与宪法“分页查询使用 page 和 size 参数，排序使用 sort 参数”一致。
- 与现有 SystemSetting 分页接口的 `page`、`size` 语义一致，便于统一。
- 单字段 `sort` 便于作为 QueryParam 传递，且可扩展为多字段（如 `sort=createTime,desc&sort=username,asc`）由实现解析。

### 3. 与 SystemSetting 现有分页的对齐方式

**决策**：保留现有 `GET /api/v1/system-settings/page` 路径不变，仅在“请求语义”上对齐通用规范。

**具体**：
- 保持 `@RequestParam page, size` 或改为接收通用分页 DTO 中的字段（page、size、sort），保证与通用分页请求语义一致。
- 响应已使用 `PageResult<SystemSettingVO>`，无需改动。
- 若后续统一为“所有分页接口均用 QueryParam page/size/sort”，则 system-settings 的 Controller 显式使用与通用 DTO 一致的参数名与默认值即可。

**理由**：不破坏现有调用方，仅做请求参数与通用规范对齐，降低变更风险。

### 4. 各业务查询 DTO 与分页的关系

**决策**：业务查询条件与分页参数分离；Controller 层同时接收“通用分页参数”与“业务 QueryDTO”。

**具体**：
- 用户：`UserQueryDTO`（如 username、status 等）+ page、size、sort（与 PageQueryDTO 一致）。
- 角色：`RoleQueryDTO`（如 roleCode、roleName 等）+ page、size、sort。
- 权限：`PermissionQueryDTO`（如 permissionCode、permissionName 等）+ page、size、sort。
- 各 Controller 可从 RequestParam 绑定 page/size/sort，或引入一个“包含分页字段”的基类/组合对象供各 QueryDTO 复用；Service 层接收业务 DTO + 分页参数（或接收组合后的 DTO）。

**理由**：业务条件与分页解耦，符合“分页请求可与各业务查询条件组合使用”的规格要求，且便于在 common 中只定义“纯分页”的 PageQueryDTO，业务模块按需扩展。

### 5. 排序字段安全与默认排序

**决策**：
- 排序字段由后端白名单控制，只允许对实体已存在且允许排序的字段（如 createTime、username、roleCode）进行排序，避免 SQL 注入与无效字段。
- 各接口约定默认排序（如按主键或 createTime 降序），未传 `sort` 时使用默认排序。

**理由**：安全与行为可预期；与 MyBatis-Plus 等使用排序字段白名单的常见做法一致。

## 结论

以上决策已覆盖通用分页 DTO 落位、排序格式、与 SystemSetting 对齐方式及业务 DTO 与分页的关系，无待澄清项，可进入 Phase 1 设计。
