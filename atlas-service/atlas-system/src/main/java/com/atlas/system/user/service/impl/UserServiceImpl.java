/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.enums.UserStatus;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.model.entity.User;
import com.atlas.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
