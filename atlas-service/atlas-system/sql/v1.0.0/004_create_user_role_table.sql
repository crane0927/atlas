-- SQL 脚本: 创建用户角色关联表
-- 版本: v1.0.0
-- 日期: 2026-01-06
-- 说明: 创建用户角色关联表 (sys_user_role)，建立用户与角色的多对多关联关系

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(role_id) ON DELETE CASCADE
);

-- 创建用户角色关联表索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_user_role ON sys_user_role(user_id, role_id);
CREATE INDEX IF NOT EXISTS idx_user_id ON sys_user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_role_id ON sys_user_role(role_id);

-- 添加表注释
COMMENT ON TABLE sys_user_role IS '用户角色关联表';
COMMENT ON COLUMN sys_user_role.id IS '关联ID';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_user_role.created_at IS '创建时间';
