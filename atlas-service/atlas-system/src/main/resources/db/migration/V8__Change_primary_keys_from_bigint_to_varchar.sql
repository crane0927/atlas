-- Flyway 迁移脚本 V8: 主键及外键列由 BIGINT 改为 VARCHAR(32)，与实体 String 类型一致
-- 说明: 先删除外键约束，再 ALTER 各列 USING column::text，最后恢复外键

-- ============================================
-- 1. 删除外键约束
-- ============================================
ALTER TABLE sys_user_role DROP CONSTRAINT IF EXISTS fk_user_role_user;
ALTER TABLE sys_user_role DROP CONSTRAINT IF EXISTS fk_user_role_role;
ALTER TABLE sys_role_permission DROP CONSTRAINT IF EXISTS fk_role_permission_role;
ALTER TABLE sys_role_permission DROP CONSTRAINT IF EXISTS fk_role_permission_permission;

-- ============================================
-- 2. 主表主键改为 VARCHAR(32)
-- ============================================
ALTER TABLE sys_user ALTER COLUMN user_id TYPE VARCHAR(32) USING user_id::text;
ALTER TABLE sys_role ALTER COLUMN role_id TYPE VARCHAR(32) USING role_id::text;
ALTER TABLE sys_permission ALTER COLUMN permission_id TYPE VARCHAR(32) USING permission_id::text;

-- ============================================
-- 3. 关联表主键与外键改为 VARCHAR(32)
-- ============================================
ALTER TABLE sys_user_role ALTER COLUMN id TYPE VARCHAR(32) USING id::text;
ALTER TABLE sys_user_role ALTER COLUMN user_id TYPE VARCHAR(32) USING user_id::text;
ALTER TABLE sys_user_role ALTER COLUMN role_id TYPE VARCHAR(32) USING role_id::text;

ALTER TABLE sys_role_permission ALTER COLUMN id TYPE VARCHAR(32) USING id::text;
ALTER TABLE sys_role_permission ALTER COLUMN role_id TYPE VARCHAR(32) USING role_id::text;
ALTER TABLE sys_role_permission ALTER COLUMN permission_id TYPE VARCHAR(32) USING permission_id::text;

-- ============================================
-- 4. 系统设置表
-- ============================================
ALTER TABLE sys_system_setting ALTER COLUMN setting_id TYPE VARCHAR(32) USING setting_id::text;

-- ============================================
-- 5. 菜单表
-- ============================================
ALTER TABLE sys_menu ALTER COLUMN menu_id TYPE VARCHAR(32) USING menu_id::text;
ALTER TABLE sys_menu ALTER COLUMN parent_id TYPE VARCHAR(32) USING parent_id::text;

-- ============================================
-- 6. 恢复外键约束
-- ============================================
ALTER TABLE sys_user_role
  ADD CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id) ON DELETE CASCADE;
ALTER TABLE sys_user_role
  ADD CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(role_id) ON DELETE CASCADE;
ALTER TABLE sys_role_permission
  ADD CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES sys_role(role_id) ON DELETE CASCADE;
ALTER TABLE sys_role_permission
  ADD CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES sys_permission(permission_id) ON DELETE CASCADE;
