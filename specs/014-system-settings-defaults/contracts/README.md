# 接口合约说明

## 概述

本功能新增系统默认设置管理接口，提供查询、创建、修改、删除能力，遵循 RESTful 规范并统一返回 `Result<T>`。

## 资源路径

- `/api/v1/system-settings`

## 接口列表

### 1. 查询设置项列表

- **方法**: GET
- **路径**: `/api/v1/system-settings`
- **查询参数**:
  - `type`（可选）: SYSTEM / CUSTOM
  - `keyword`（可选）: key 关键字
- **响应**: `Result<List<SystemSettingVO>>`

### 1.1 分页查询设置项列表

- **方法**: GET
- **路径**: `/api/v1/system-settings/page`
- **查询参数**:
  - `page`（可选）: 页码（从 1 开始，默认 1）
  - `size`（可选）: 每页大小（默认 10）
  - `type`（可选）: SYSTEM / CUSTOM
  - `keyword`（可选）: key 关键字
- **响应**: `Result<PageResult<SystemSettingVO>>`

### 2. 新增自定义设置项

- **方法**: POST
- **路径**: `/api/v1/system-settings`
- **请求体**: `SystemSettingCreateDTO`
- **响应**: `Result<SystemSettingVO>`
- **规则**: 仅允许创建 CUSTOM 类型

### 3. 修改设置项 value

- **方法**: PUT
- **路径**: `/api/v1/system-settings/{key}`
- **请求体**: `SystemSettingUpdateDTO`
- **响应**: `Result<SystemSettingVO>`
- **规则**: SYSTEM 类型仅允许修改 value，不允许修改 key

### 4. 删除自定义设置项

- **方法**: DELETE
- **路径**: `/api/v1/system-settings/{key}`
- **响应**: `Result<Boolean>`
- **规则**: SYSTEM 类型删除请求必须拒绝

## 错误场景

- 删除 SYSTEM 类型：返回“系统默认设置不可删除”
- 新增时 key 重复：返回“key 已存在”
- 无权限操作：返回“无操作权限”
- 设置项不存在：返回“设置项不存在”
