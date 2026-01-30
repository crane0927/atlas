# 快速开始

## 适用范围

本指南用于 `atlas-system` 中“系统默认设置”功能的本地验证与联调准备。

## 前置条件

- JDK 21
- PostgreSQL 已可用
- 本地已配置基础运行环境（Nacos、配置中心等）

## 关键步骤

1. 按项目规范创建数据库迁移脚本并执行（Flyway）
2. 确认设置项表已创建且包含唯一性约束
3. 启动 `atlas-system` 服务
4. 通过接口完成以下验证：
   - 查询设置项列表
   - 新增自定义设置项
   - 修改系统类型设置项的 value
   - 删除自定义设置项

## 验证要点

- SYSTEM 类型不可删除
- CUSTOM 类型可新增、修改、删除
- key 重复时返回明确提示
- 变更记录包含操作者与时间

## 示例请求

1. 查询设置项列表（可选筛选）：
   ```bash
   curl "http://localhost:8085/api/v1/system-settings?type=SYSTEM&keyword=site"
   ```

2. 分页查询设置项列表：
   ```bash
   curl "http://localhost:8085/api/v1/system-settings/page?page=1&size=10&type=SYSTEM&keyword=site"
   ```

3. 新增自定义设置项：
   ```bash
   curl -X POST "http://localhost:8085/api/v1/system-settings" \
     -H "Content-Type: application/json" \
     -d '{
       "key": "theme.color",
       "value": "blue"
     }'
   ```

4. 修改系统类型设置项 value：
   ```bash
   curl -X PUT "http://localhost:8085/api/v1/system-settings/site.name" \
     -H "Content-Type: application/json" \
     -d '{
       "value": "Atlas Pro"
     }'
   ```

5. 删除自定义设置项：
   ```bash
   curl -X DELETE "http://localhost:8085/api/v1/system-settings/theme.color"
   ```
