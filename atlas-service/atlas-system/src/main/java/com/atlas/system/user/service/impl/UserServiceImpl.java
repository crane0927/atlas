/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.enums.UserStatus;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.role.mapper.RoleMapper;
import com.atlas.system.role.model.entity.Role;
import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.mapper.UserRoleMapper;
import com.atlas.system.user.model.dto.UserCreateDTO;
import com.atlas.system.user.model.dto.UserQueryDTO;
import com.atlas.system.user.model.entity.User;
import com.atlas.system.user.model.entity.UserRole;
import com.atlas.system.user.model.vo.UserListVO;
import com.atlas.system.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 *
 * <p>实现用户查询业务逻辑，包括根据用户ID和用户名查询用户信息。
 *
 * <p>功能说明：
 *
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
   * 验证用户密码
   *
   * @param username 用户名
   * @param password 明文密码
   * @return 加密后的密码，如果用户不存在或密码错误则抛出 BusinessException
   */
  @Override
  public String verifyPassword(String username, String password) {
    User user = userMapper.selectByUsername(username);
    if (user == null) {
      throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在");
    }
    // 验证密码
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户名或密码错误");
    }
    // 返回加密后的密码（用于 Auth 服务后续验证）
    return user.getPassword();
  }

  /**
   * 将 User 实体转换为 UserDTO（原则 20：使用 BeanUtils，status 需手写转枚举）
   *
   * @param user 用户实体
   * @return 用户 DTO
   */
  private UserDTO convertToDTO(User user) {
    UserDTO dto = new UserDTO();
    BeanUtils.copyProperties(user, dto);
    dto.setStatus(convertStatus(user.getStatus()));
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
    // 保存用户（createdAt/updatedAt 由 AuditMetaObjectHandler 填充）
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
   * 分页查询用户列表
   *
   * @param query 查询条件
   * @return 分页结果
   */
  @Override
  public PageResult<UserListVO> listUsersPage(UserQueryDTO query) {
    int pageNum = Optional.ofNullable(query).map(UserQueryDTO::getPageSafe).orElse(1);
    int pageSize = Optional.ofNullable(query).map(UserQueryDTO::getSizeSafe).orElse(10);
    String sort = Optional.ofNullable(query).map(UserQueryDTO::getSort).orElse(null);

    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    wrapper.ne(User::getStatus, "DELETED");
    Optional.ofNullable(query)
        .ifPresent(
            q -> {
              if (StringUtils.hasText(q.getUsername())) {
                wrapper.like(User::getUsername, q.getUsername());
              }
              if (StringUtils.hasText(q.getStatus())) {
                wrapper.eq(User::getStatus, q.getStatus());
              }
            });
    applySort(wrapper, sort);

    Page<User> pageReq = new Page<>(pageNum, pageSize);
    Page<User> resultPage = userMapper.selectPage(pageReq, wrapper);
    List<UserListVO> list =
        resultPage.getRecords().stream().map(this::convertToListVO).collect(Collectors.toList());
    return PageResult.of(list, resultPage.getTotal(), pageNum, pageSize);
  }

  /**
   * 应用排序（白名单：createdAt、username，兼容 createTime/createdAt）
   *
   * @param wrapper 查询包装器
   * @param sort 排序字符串，格式：字段名,asc 或 字段名,desc
   */
  private void applySort(LambdaQueryWrapper<User> wrapper, String sort) {
    if (!StringUtils.hasText(sort)) {
      wrapper.orderByDesc(User::getCreatedAt);
      return;
    }
    String[] parts = sort.split(",");
    String field = parts.length > 0 ? parts[0].trim() : "";
    boolean asc = parts.length <= 1 || !"desc".equalsIgnoreCase(parts[1].trim());
    if ("username".equalsIgnoreCase(field)) {
      wrapper.orderBy(true, asc, User::getUsername);
    } else if ("createTime".equalsIgnoreCase(field) || "createdAt".equalsIgnoreCase(field)) {
      wrapper.orderBy(true, asc, User::getCreatedAt);
    } else {
      wrapper.orderByDesc(User::getCreatedAt);
    }
  }

  /**
   * 将 User 实体转换为 UserListVO（原则 20：使用 BeanUtils）
   *
   * @param user 用户实体
   * @return 列表项 VO
   */
  private UserListVO convertToListVO(User user) {
    UserListVO vo = new UserListVO();
    BeanUtils.copyProperties(user, vo);
    vo.setCreatedAt(user.getCreatedAt());
    return vo;
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
