/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.enums.UserStatus;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.role.mapper.RoleMapper;
import com.atlas.system.role.model.entity.Role;
import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.mapper.UserRoleMapper;
import com.atlas.system.user.model.dto.UserCreateDTO;
import com.atlas.system.user.model.entity.User;
import com.atlas.system.user.model.entity.UserRole;
import com.atlas.system.user.service.UserService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 *
 * <p>实现用户查询业务逻辑，包括根据用户ID和用户名查询用户信息。
 *
 * <p>功能说明：
 * <ul>
 *   <li>实现 getUserById 方法，根据用户ID查询用户
 *   <li>实现 getUserByUsername 方法，根据用户名查询用户
 *   <li>实现 Entity 到 DTO 的转换
 *   <li>处理用户不存在的情况，抛出 BusinessException
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final UserRoleMapper userRoleMapper;
  private final RoleMapper roleMapper;
  private final PasswordEncoder passwordEncoder;

  /**
   * 根据用户ID查询用户信息
   *
   * @param userId 用户ID
   * @return 用户信息 DTO
   * @throws BusinessException 如果用户不存在，错误码：032001
   */
  @Override
  public UserDTO getUserById(Long userId) {
    User user = userMapper.selectById(userId);
    if (user == null || "DELETED".equals(user.getStatus())) {
      throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在");
    }
    return convertToDTO(user);
  }

  /**
   * 根据用户名查询用户信息
   *
   * @param username 用户名
   * @return 用户信息 DTO
   * @throws BusinessException 如果用户不存在，错误码：032001
   */
  @Override
  public UserDTO getUserByUsername(String username) {
    User user = userMapper.selectByUsername(username);
    if (user == null) {
      throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在");
    }
    return convertToDTO(user);
  }

  /**
   * 将 User 实体转换为 UserDTO
   *
   * @param user 用户实体
   * @return 用户 DTO
   */
  private UserDTO convertToDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setUserId(user.getUserId());
    dto.setUsername(user.getUsername());
    dto.setNickname(user.getNickname());
    dto.setEmail(user.getEmail());
    dto.setPhone(user.getPhone());
    dto.setStatus(convertStatus(user.getStatus()));
    dto.setAvatar(user.getAvatar());
    return dto;
  }

  /**
   * 创建用户
   *
   * @param userCreateDTO 用户创建 DTO
   * @return 用户信息 DTO
   * @throws BusinessException 如果用户名已存在，错误码：032004
   */
  @Override
  @Transactional
  public UserDTO createUser(UserCreateDTO userCreateDTO) {
    // 检查用户名是否已存在
    User existingUser = userMapper.selectByUsername(userCreateDTO.getUsername());
    if (existingUser != null) {
      throw new BusinessException(SystemErrorCode.USERNAME_ALREADY_EXISTS, "用户名已存在");
    }
    // 创建用户实体
    User user = new User();
    user.setUsername(userCreateDTO.getUsername());
    user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
    user.setNickname(userCreateDTO.getNickname());
    user.setEmail(userCreateDTO.getEmail());
    user.setPhone(userCreateDTO.getPhone());
    user.setStatus("ACTIVE");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    // 保存用户
    userMapper.insert(user);
    // 返回用户 DTO
    return convertToDTO(user);
  }

  /**
   * 为用户分配角色
   *
   * @param userId 用户ID
   * @param roleId 角色ID
   * @throws BusinessException 如果用户或角色不存在，错误码：032001 或 032101
   */
  @Override
  @Transactional
  public void assignRoleToUser(Long userId, Long roleId) {
    // 检查用户是否存在
    User user = userMapper.selectById(userId);
    if (user == null || "DELETED".equals(user.getStatus())) {
      throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在");
    }
    // 检查角色是否存在
    Role role = roleMapper.selectById(roleId);
    if (role == null || "DELETED".equals(role.getStatus())) {
      throw new BusinessException(SystemErrorCode.ROLE_NOT_FOUND, "角色不存在");
    }
    // 检查关联是否已存在
    UserRole existingUserRole =
        userRoleMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleId, roleId));
    if (existingUserRole != null) {
      return; // 关联已存在，直接返回
    }
    // 创建用户角色关联
    UserRole userRole = new UserRole();
    userRole.setUserId(userId);
    userRole.setRoleId(roleId);
    userRole.setCreatedAt(LocalDateTime.now());
    userRoleMapper.insert(userRole);
  }

  /**
   * 将数据库状态字符串转换为 UserStatus 枚举
   *
   * @param status 数据库状态字符串
   * @return UserStatus 枚举值
   */
  private UserStatus convertStatus(String status) {
    if (status == null) {
      return UserStatus.INACTIVE;
    }
    try {
      return UserStatus.valueOf(status);
    } catch (IllegalArgumentException e) {
      return UserStatus.INACTIVE;
    }
  }
}
