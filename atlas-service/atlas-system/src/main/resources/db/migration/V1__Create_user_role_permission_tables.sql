-- Flyway 迁移脚本 V1: 创建用户、角色、权限相关表
-- 版本: v1.0.0
-- 日期: 2026-01-06
-- 说明: 创建用户表、角色表、权限表及其关联表

-- ============================================
-- 1. 创建用户表 (sys_user)
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    avatar VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户表索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_status ON sys_user(status);

-- 添加表注释
COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.user_id IS '用户ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码（加密）';
COMMENT ON COLUMN sys_user.nickname IS '昵称';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.status IS '用户状态（ACTIVE/INACTIVE/LOCKED/DELETED）';
COMMENT ON COLUMN sys_user.avatar IS '头像URL';
COMMENT ON COLUMN sys_user.created_at IS '创建时间';
COMMENT ON COLUMN sys_user.updated_at IS '更新时间';

-- ============================================
-- 2. 创建角色表 (sys_role)
-- ============================================
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

-- ============================================
-- 3. 创建权限表 (sys_permission)
-- ============================================
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

-- ============================================
-- 4. 创建用户角色关联表 (sys_user_role)
-- ============================================
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

-- ============================================
-- 5. 创建角色权限关联表 (sys_role_permission)
-- ============================================
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
