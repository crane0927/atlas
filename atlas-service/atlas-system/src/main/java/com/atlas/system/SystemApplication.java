/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Atlas System 服务启动类
 *
 * <p>提供用户、角色、权限管理功能，支持 Auth 服务查询用户信息和权限信息。
 *
 * <p>主要功能：
 *
 * <ul>
 *   <li>用户查询：支持根据用户ID和用户名查询用户信息
 *   <li>权限查询：支持查询用户角色和权限信息
 *   <li>用户管理：支持创建用户、分配角色等管理功能
 * </ul>
 *
 * <p>扫描 com.atlas.auth 以加载 SecurityConfig、SecurityContextFilter 等，使本进程内请求可解析 Token
 * 并设置当前用户，审计字段 createBy/updateBy 能拿到当前用户名。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.atlas.system", "com.atlas.auth"})
@MapperScan("com.atlas.system.**.mapper")
public class SystemApplication {

  public static void main(String[] args) {
    SpringApplication.run(SystemApplication.class, args);
  }
}
