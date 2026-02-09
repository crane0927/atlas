# Nacos 配置

本目录下的 YAML 为 Gateway 在 Nacos 中的配置内容，需在 Nacos 控制台创建/更新后，Gateway 通过 `spring.config.import` 加载。

## 使用方式

1. 打开 Nacos 控制台（如 http://localhost:8848/nacos）
2. 进入 **配置管理** → **配置列表**
3. 新建或编辑配置：
   - **Data ID**: `atlas-gateway-dev.yaml`
   - **Group**: `DEFAULT_GROUP`
   - **配置格式**: YAML
   - **配置内容**: 复制 `atlas-gateway-dev.yaml` 的完整内容粘贴保存

本地启动前请确保 Nacos 中已存在该配置，否则路由、白名单、鉴权等将使用不到，请求可能无法正确转发或鉴权失败。
