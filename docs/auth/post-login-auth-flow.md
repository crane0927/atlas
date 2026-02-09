# 登录后接口鉴权流程说明

## 一、Redis Session 与登录响应的关系

你看到的 Redis `session:1` 中的 `"token":"d929e2c3-916e-4e2b-b08b-523263e3adf5"` 实际存储的是 **JWT 的 tokenId（jti）**，不是完整的 JWT 字符串。

- **登录响应**：Auth 服务返回的 `LoginResponseVO` 里 `token` 字段是 **完整的 JWT 字符串**（形如 `eyJhbGciOiJSUzI1NiIs...`），客户端应保存并用于后续请求。
- **Redis session**：仅用于服务端会话与登出/黑名单。参见 [SessionServiceImpl.java](../../atlas-auth/src/main/java/com/atlas/auth/service/impl/SessionServiceImpl.java) 第 51 行：`sessionData.put("token", tokenInfo.getTokenId())`，即存的是 JWT 里的 `jti`。
- **结论**：后续接口鉴权时，客户端必须在请求头里携带 **登录接口返回的那段 JWT**，而不是 Redis 里这段 tokenId。

---

## 二、后续接口鉴权整体流程

```mermaid
sequenceDiagram
  participant Client
  participant Gateway
  participant Validator as TokenValidator_JWT_or_Introspect
  participant Auth as Auth_Service_Optional
  participant Backend as 下游业务服务

  Client->>Gateway: GET /api/xxx  Header: Authorization: Bearer &lt;JWT&gt;
  Gateway->>Gateway: 白名单? 是则放行
  Gateway->>Validator: validate(exchange)
  alt JWT 模式
    Validator->>Validator: 公钥验签 JWT，解析 userId/username/roles/permissions
  else Introspection 模式
    Validator->>Auth: POST /api/v1/auth/introspect  body: token
    Auth->>Auth: 验签 + 黑名单检查
    Auth-->>Validator: active, userId, username, roles, permissions
  end
  Validator-->>Gateway: 写入 X-User-Id, X-Username, X-User-Roles, X-User-Permissions
  Gateway->>Backend: 转发请求并携带上述请求头
  Backend->>Backend: SecurityContextFilter 从请求头或 Token 构建 LoginUser
  Backend-->>Client: 业务响应
```

---

## 三、客户端如何携带鉴权信息

后续所有需要鉴权的请求，在请求头中携带：

- **Header 名**：`Authorization`
- **值**：`Bearer <登录接口返回的 token（完整 JWT）>`

示例：

```http
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

只有携带该 JWT 的请求才会通过 Gateway 的 Token 校验（非白名单路径）。

---

## 四、Gateway 鉴权（两种模式）

由 [AuthGatewayFilter](../../atlas-gateway/src/main/java/com/atlas/gateway/filter/AuthGatewayFilter.java) 统一入口：先判断白名单，非白名单则调用 `GatewayTokenValidator.validate(exchange)`。

### 1. JWT 模式（默认，配置了公钥且未用 introspection）

- **实现**：[JwtGatewayTokenValidator](../../atlas-gateway/src/main/java/com/atlas/gateway/filter/JwtGatewayTokenValidator.java)
- **逻辑**：从 `Authorization: Bearer` 取出 JWT → 用配置的 RSA 公钥验签并解析 Claims → 校验通过后往转发请求中写入：
  - `X-User-Id`
  - `X-Username`
  - `X-User-Roles`（逗号分隔）
  - `X-User-Permissions`（逗号分隔）
- **特点**：**不访问 Redis、不调用 Auth**，纯本地验签，无黑名单检查（黑名单在登出/Introspect 时由 Auth 使用）。

启用条件（见 [GatewayJwtConfiguration](../../atlas-gateway/src/main/java/com/atlas/gateway/config/GatewayJwtConfiguration.java)）：  
`atlas.gateway.auth.jwt.public-key` 非空且 `atlas.gateway.auth.validation-mode != 'introspection'`。

### 2. Introspection 模式

- **实现**：[IntrospectGatewayTokenValidator](../../atlas-gateway/src/main/java/com/atlas/gateway/filter/IntrospectGatewayTokenValidator.java)
- **逻辑**：从 `Authorization: Bearer` 取出 token → 调用 Auth 的 `POST /api/v1/auth/introspect`，Auth 内部会：
  - 解析并验签 JWT
  - 用 `sessionService.isBlacklisted(tokenId)` 查 Redis 黑名单（登出时写入）
- **特点**：每次请求都会问 Auth，可做黑名单校验；通过后 Gateway 同样写入上述四个请求头。

---

## 五、下游业务服务如何拿到当前用户

经过 Gateway 的请求会带上 `X-User-Id`、`X-Username`、`X-User-Roles`、`X-User-Permissions`。

- **常规路径**（请求经 Gateway）：[SecurityContextFilter](../../atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/security/SecurityContextFilter.java) 优先用这四个请求头构建 `LoginUser` 并放入安全上下文，业务代码通过 `SecurityContext` 获取当前用户。
- **直连下游**（不经过 Gateway）：若没有上述请求头，同一 Filter 会从 `Authorization: Bearer` 取出 token，交给各服务自己的 `TokenValidator`（如 Auth 的 [TokenValidatorAdapter](../../atlas-auth/src/main/java/com/atlas/auth/validator/TokenValidatorAdapter.java)）校验，通过后同样设置 `LoginUser`。

---

## 六、Redis Session 在鉴权中的角色

| 用途       | 说明                                                                                                                                  |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| 会话存储   | `session:{userId}` 存当前登录的 tokenId、loginTime、expiresAt 等，用于会话管理。                                                       |
| 登出/黑名单 | 登出时 Auth 将 tokenId 写入 `token:blacklist:{tokenId}`；JWT 模式下的 Gateway **不查**黑名单，只有 Introspection 或下游用 TokenValidator 时才会通过 Auth 查到黑名单。 |
| 后续请求鉴权 | **不依赖** Redis session 内容做“是否放行”；放行与否由 **JWT 有效性**（及可选黑名单）决定。客户端带的是 JWT，不是 Redis 里的 tokenId。   |

---

## 七、小结

- 客户端：后续接口统一带 **Authorization: Bearer &lt;登录返回的 JWT&gt;**。
- Gateway：非白名单路径用 JWT 或 Introspection 校验 token，通过后转发并附加 `X-User-Id` 等四个请求头。
- Redis session：存的是 tokenId 和会话元数据，用于会话与黑名单，不用于“带什么值去鉴权”；鉴权始终用 **JWT 字符串**。

若你希望“登出后立刻失效”，当前 JWT 模式下 Gateway 不会查黑名单，需改为使用 **Introspection 模式** 或在 Gateway 侧增加对 Auth 黑名单/会话的校验。

---

## 八、Introspection 模式详细说明

### 8.1 适用场景与特点

- **适用**：需要“登出即失效”、或集中校验（含黑名单）时使用。
- **特点**：每次鉴权都会调用 Auth 的 `/api/v1/auth/introspect`，Auth 会验签 JWT 并查 Redis 黑名单（`token:blacklist:{tokenId}`），登出过的 token 会返回 `active: false`，Gateway 拒绝请求。

### 8.2 启用条件

需同时满足（见 [IntrospectGatewayConfiguration](../../atlas-gateway/src/main/java/com/atlas/gateway/config/IntrospectGatewayConfiguration.java)）：

- `atlas.gateway.auth.validation-mode == 'introspection'`（注意是字符串 `introspection`，不是 `jwt`）
- `atlas.gateway.auth.introspect.url` 非空

满足时注册 `IntrospectGatewayTokenValidator` 为 `@Primary`，且 [GatewayJwtConfiguration](../../atlas-gateway/src/main/java/com/atlas/gateway/config/GatewayJwtConfiguration.java) 在 introspection 下不会加载，不会注册 JWT 校验器。

### 8.3 Gateway 端配置

在 Nacos 或 `application.yml` 中配置（参考 [atlas-gateway-dev.yaml](../../atlas-gateway/src/main/resources/nacos/atlas-gateway-dev.yaml)）：

```yaml
atlas:
  gateway:
    auth:
      validation-mode: introspection
      introspect:
        url: http://<atlas-auth 地址>/atlas-auth/api/v1/auth/introspect   # 必填，含 context-path
        api-key: ${ATLAS_GATEWAY_INTROSPECT_API_KEY:}         # 可选，与 Auth 端一致时启用服务间认证
```

- **url**：Auth 服务提供的 Introspection 接口完整 URL（如 `http://localhost:8084/atlas-auth/api/v1/auth/introspect`，需包含 Auth 的 context-path `/atlas-auth`）。
- **api-key**：可选。若配置，Gateway 调用 introspect 时会在请求头中带 `X-Introspect-Api-Key: <api-key>`，Auth 端可校验该头，防止接口被任意调用。

### 8.4 Auth 端 Introspection 接口

- **路径**：`POST /api/v1/auth/introspect`
- **请求体**：`{ "token": "<客户端携带的 JWT 字符串>" }`
- **Auth 内部逻辑**（[AuthController#introspect](../../atlas-auth/src/main/java/com/atlas/auth/controller/AuthController.java)）：
  - 调用 `tokenService.validateToken(request.getToken())`：
    - 解析并验签 JWT（格式、签名、过期时间）
    - 用 `sessionService.isBlacklisted(tokenId)` 查 Redis，若在黑名单则返回 null
  - 若有效：返回 `active: true` 及 `userId`、`username`、`roles`、`permissions`、`expiresAt`
  - 若无效或已登出：返回 `active: false`（无用户信息字段）
- **响应示例（有效）**：  
  `data.active=true`，且含 `userId`、`username`、`roles`、`permissions`、`expiresAt`。
- **响应示例（无效）**：  
  `data.active=false`。

### 8.5 Auth 端服务间认证（可选）

若希望仅 Gateway（或可信服务）能调 Introspection 接口，可在 Auth 配置 API Key（[AuthProperties](../../atlas-auth/src/main/java/com/atlas/auth/config/AuthProperties.java)）：

```yaml
atlas:
  auth:
    introspect:
      api-key: <与 Gateway 配置相同的密钥>
```

- 配置非空时，[IntrospectAuthFilter](../../atlas-auth/src/main/java/com/atlas/auth/filter/IntrospectAuthFilter.java) 会对 `POST /api/v1/auth/introspect` 校验请求头 `X-Introspect-Api-Key` 与配置值一致，否则返回 401。
- Gateway 侧 `atlas.gateway.auth.introspect.api-key` 与上述值保持一致即可。

### 8.6 Gateway 校验流程（Introspection 模式）

1. 从请求中取 `Authorization: Bearer <token>`，无 token 则返回 401。
2. 使用 WebClient 调用 Auth：  
   `POST introspectUrl`，Body `{"token": "<token>"}`，若配置了 api-key 则 Header `X-Introspect-Api-Key: <api-key>`。
3. 解析响应为 `Result<IntrospectDataDto>`；若调用失败、或 `data == null`、或 `data.active != true`，则校验失败，返回 401。
4. 校验通过后，将 `data` 中的 `userId`、`username`、`roles`、`permissions` 写入转发请求头（与 JWT 模式相同的四个头），再转发到下游。

### 8.7 与 JWT 模式对比

| 项目           | JWT 模式             | Introspection 模式                            |
| -------------- | -------------------- | --------------------------------------------- |
| 校验位置       | Gateway 本地公钥验签 | Gateway 调用 Auth `/introspect`               |
| 是否查黑名单   | 否                   | 是（Auth 查 Redis `token:blacklist:{tokenId}`） |
| 登出后是否立刻失效 | 否（需等 JWT 过期） | 是                                            |
| 依赖           | 只需 Gateway 配置公钥 | 需 Auth 服务可用、且配置 url（可选 api-key）  |
| 性能           | 无网络调用           | 每次鉴权一次 HTTP 调用                        |
