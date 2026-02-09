# Nacos 配置

本目录下的 YAML 为 System 在 Nacos 中的配置内容，需在 Nacos 控制台创建/更新后，System 通过 `spring.config.import` 加载。

## 使用方式

1. 打开 Nacos 控制台（如 http://localhost:8848/nacos）
2. 进入 **配置管理** → **配置列表**
3. 新建或编辑配置：
   - **Data ID**: `atlas-system-dev.yaml`
   - **Group**: `DEFAULT_GROUP`
   - **配置格式**: YAML
   - **配置内容**: 复制 `atlas-system-dev.yaml` 的完整内容粘贴保存

数据库密码等敏感项建议通过环境变量（如 `SPRING_DATASOURCE_PASSWORD`）注入。
