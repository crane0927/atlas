# 快速开始：通用分页与 atlas-system 分页接口

## 概述

本文档说明如何调用通用分页请求规范及 atlas-system 下的用户、角色、权限、系统设置分页查询接口。

## 前置条件

- atlas-system 服务已启动（如本地 `http://localhost:8085` 或经网关转发）。
- 调用方已具备所需鉴权（如 Bearer Token），若接口受保护。

## 通用分页参数

所有分页接口均支持以下 Query 参数（语义一致）：

| 参数 | 说明 | 示例 |
|------|------|------|
| page | 当前页码，从 1 开始 | `page=1` |
| size | 每页条数 | `size=10` |
| sort | 排序，格式为 `字段名,方向` | `sort=createTime,desc` |

## 调用示例

### 用户分页列表

```bash
# 第一页，每页 10 条，按创建时间倒序
curl -s "http://localhost:8085/api/v1/users?page=1&size=10&sort=createTime,desc"

# 带条件：用户名包含 admin、状态为 ACTIVE
curl -s "http://localhost:8085/api/v1/users?page=1&size=10&username=admin&status=ACTIVE"
```

### 角色分页列表

```bash
curl -s "http://localhost:8085/api/v1/roles?page=1&size=10&sort=roleCode,asc"
```

### 权限分页列表

```bash
curl -s "http://localhost:8085/api/v1/permissions?page=1&size=10&sort=permissionName,asc"
```

### 系统设置分页列表

```bash
# 与通用分页参数一致
curl -s "http://localhost:8085/api/v1/system-settings/page?page=1&size=10&sort=key,asc"
```

## 响应示例

所有分页接口均返回 `Result<PageResult<T>>` 结构，例如：

```json
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "list": [ ... ],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10,
    "traceId": "xxx"
  }
}
```

## 注意事项

- 排序字段需为后端白名单允许的字段，否则可能被忽略或返回 400。
- size 建议不超过 100，具体上限以服务端配置为准。
- 列表接口可能受权限控制，未授权时返回 401/403。
