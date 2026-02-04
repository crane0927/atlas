# 改造清单：宪法原则 20/21

## 改造类型：转换（原则 20）

| 文件路径 | 方法名 | 说明 |
|----------|--------|------|
| atlas-service/atlas-system/src/main/java/com/atlas/system/user/service/impl/UserServiceImpl.java | convertToDTO, convertToListVO | User→UserDTO（status 需转枚举）, User→UserListVO |
| atlas-service/atlas-system/src/main/java/com/atlas/system/role/service/impl/RoleServiceImpl.java | convertToListVO | Role→RoleListVO |
| atlas-service/atlas-system/src/main/java/com/atlas/system/permission/service/impl/PermissionServiceImpl.java | convertToListVO | Permission→PermissionListVO |
| atlas-service/atlas-system/src/main/java/com/atlas/system/settings/service/impl/SystemSettingServiceImpl.java | convertToVO | SystemSetting→SystemSettingVO（BaseEntity 字段名与 VO 可能不同） |

## 改造类型：判空（原则 21）

| 文件路径 | 方法名 | 说明 |
|----------|--------|------|
| UserServiceImpl.java | listUsersPage | query != null ? query.getPageSafe() : 1 等 |
| RoleServiceImpl.java | listRolesPage | query != null ? query.getPageSafe() : 1 等 |
| PermissionServiceImpl.java | listPermissionsPage | query != null ? query.getPageSafe() : 1 等 |
| SystemSettingServiceImpl.java | listSettingsPage, buildQueryWrapper | queryDTO != null ? ... 等 |

## 状态

- 转换改造：已实施（T004～T007）
- 判空改造：已实施（T009～T014）
