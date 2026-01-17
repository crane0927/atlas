-- SQL 脚本: 创建角色权限关联表
-- 版本: v1.0.0
-- 日期: 2026-01-06
-- 说明: 创建角色权限关联表 (sys_role_permission)，建立角色与权限的多对多关联关系

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES sys_role(role_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES sys_permission(permission_id) ON DELETE CASCADE
);

-- 创建角色权限关联表索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_role_permission ON sys_role_permission(role_id, permission_id);
CREATE INDEX IF NOT EXISTS idx_role_permission_role_id ON sys_role_permission(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permission_permission_id ON sys_role_permission(permission_id);

-- 添加表注释
COMMENT ON TABLE sys_role_permission IS '角色权限关联表';
COMMENT ON COLUMN sys_role_permission.id IS '关联ID';
COMMENT ON COLUMN sys_role_permission.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_permission.permission_id IS '权限ID';
COMMENT ON COLUMN sys_role_permission.created_at IS '创建时间';
