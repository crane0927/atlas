-- SQL 脚本: 创建权限表
-- 版本: v1.0.0
-- 日期: 2026-01-06
-- 说明: 创建权限表 (sys_permission)，包含权限基本信息字段和索引

-- 创建权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    permission_id BIGSERIAL PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL,
    permission_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建权限表索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_permission_code ON sys_permission(permission_code);
CREATE INDEX IF NOT EXISTS idx_permission_status ON sys_permission(status);

-- 添加表注释
COMMENT ON TABLE sys_permission IS '权限表';
COMMENT ON COLUMN sys_permission.permission_id IS '权限ID';
COMMENT ON COLUMN sys_permission.permission_code IS '权限代码';
COMMENT ON COLUMN sys_permission.permission_name IS '权限名称';
COMMENT ON COLUMN sys_permission.description IS '权限描述';
COMMENT ON COLUMN sys_permission.status IS '权限状态（ACTIVE/INACTIVE/DELETED）';
COMMENT ON COLUMN sys_permission.created_at IS '创建时间';
COMMENT ON COLUMN sys_permission.updated_at IS '更新时间';
