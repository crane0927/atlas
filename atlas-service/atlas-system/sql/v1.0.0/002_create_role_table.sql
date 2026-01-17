-- SQL 脚本: 创建角色表
-- 版本: v1.0.0
-- 日期: 2026-01-06
-- 说明: 创建角色表 (sys_role)，包含角色基本信息字段和索引

-- 创建角色表
CREATE TABLE IF NOT EXISTS sys_role (
    role_id BIGSERIAL PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建角色表索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_role_code ON sys_role(role_code);
CREATE INDEX IF NOT EXISTS idx_role_status ON sys_role(status);

-- 添加表注释
COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_role.role_code IS '角色代码';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.description IS '角色描述';
COMMENT ON COLUMN sys_role.status IS '角色状态（ACTIVE/INACTIVE/DELETED）';
COMMENT ON COLUMN sys_role.created_at IS '创建时间';
COMMENT ON COLUMN sys_role.updated_at IS '更新时间';
