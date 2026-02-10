/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.service.impl;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.enums.UserStatus;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.role.mapper.RoleMapper;
import com.atlas.system.role.model.entity.Role;
import com.atlas.system.settings.mapper.SystemSettingMapper;
import com.atlas.system.settings.model.entity.SystemSetting;
import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.mapper.UserRoleMapper;
import com.atlas.system.user.model.dto.UserCreateDTO;
import com.atlas.system.user.model.dto.UserQueryDTO;
import com.atlas.system.user.model.dto.UserUpdateDTO;
import com.atlas.system.user.model.entity.User;
import com.atlas.system.user.model.entity.UserRole;
import com.atlas.system.user.model.vo.UserListVO;
import com.atlas.system.user.service.UserService;
import com.atlas.system.util.SortHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
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
    private final SystemSettingMapper systemSettingMapper;

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息 DTO
     * @throws BusinessException 如果用户不存在，错误码：032001
     */
    @Override
    public UserDTO getUserById(String userId) {
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
        SystemSetting systemSetting = systemSettingMapper.selectOne(new LambdaQueryWrapper<SystemSetting>().eq(SystemSetting::getKey, "user.default.password"));
        if (systemSetting == null) {
            throw new BusinessException(SystemErrorCode.SYSTEM_SETTING_NOT_FOUND, "设置项不存在");
        }
        // 创建用户实体
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(passwordEncoder.encode(systemSetting.getValue()));
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
     * 为用户批量分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    @Override
    @Transactional
    public void assignRolesToUser(String userId, List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        User user = userMapper.selectById(userId);
        if (user == null || "DELETED".equals(user.getStatus())) {
            throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        for (String roleId : roleIds) {
            Role role = roleMapper.selectById(roleId);
            if (role == null || "DELETED".equals(role.getStatus())) {
                throw new BusinessException(SystemErrorCode.ROLE_NOT_FOUND, "角色不存在");
            }
            UserRole existingUserRole =
                    userRoleMapper.selectOne(
                            new LambdaQueryWrapper<UserRole>()
                                    .eq(UserRole::getUserId, userId)
                                    .eq(UserRole::getRoleId, roleId));
            if (existingUserRole != null) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    /**
     * 更新用户
     *
     * @param userId 用户ID
     * @param userUpdateDTO 更新 DTO
     * @return 更新后的用户信息 DTO
     */
    @Override
    @Transactional
    public UserDTO updateUser(String userId, UserUpdateDTO userUpdateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null || "DELETED".equals(user.getStatus())) {
            throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        if (userUpdateDTO.getNickname() != null) {
            user.setNickname(userUpdateDTO.getNickname());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getPhone() != null) {
            user.setPhone(userUpdateDTO.getPhone());
        }
        if (userUpdateDTO.getStatus() != null) {
            user.setStatus(userUpdateDTO.getStatus());
        }
        if (userUpdateDTO.getAvatar() != null) {
            user.setAvatar(userUpdateDTO.getAvatar());
        }
        userMapper.updateById(user);
        return convertToDTO(user);
    }

    /**
     * 逻辑删除用户
     *
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void deleteUser(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null || "DELETED".equals(user.getStatus())) {
            throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        user.setStatus("DELETED");
        userMapper.updateById(user);
    }

    /**
     * 批量移除用户与角色的关联
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    @Override
    @Transactional
    public void removeRolesFromUser(String userId, List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        User user = userMapper.selectById(userId);
        if (user == null || "DELETED".equals(user.getStatus())) {
            throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        for (String roleId : roleIds) {
            Role role = roleMapper.selectById(roleId);
            if (role == null || "DELETED".equals(role.getStatus())) {
                throw new BusinessException(SystemErrorCode.ROLE_NOT_FOUND, "角色不存在");
            }
            userRoleMapper.delete(
                    new LambdaQueryWrapper<UserRole>()
                            .eq(UserRole::getUserId, userId)
                            .eq(UserRole::getRoleId, roleId));
        }
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

    private static final Map<String, BiConsumer<LambdaQueryWrapper<User>, Boolean>> USER_SORT_FIELDS =
            new HashMap<>();

    static {
        USER_SORT_FIELDS.put("username", (w, asc) -> w.orderBy(true, asc, User::getUsername));
        USER_SORT_FIELDS.put("createdat", (w, asc) -> w.orderBy(true, asc, User::getCreatedAt));
        USER_SORT_FIELDS.put("createtime", (w, asc) -> w.orderBy(true, asc, User::getCreatedAt));
    }

    /**
     * 应用排序（白名单：createdAt、username，兼容 createTime/createdAt）
     */
    private void applySort(LambdaQueryWrapper<User> wrapper, String sort) {
        SortHelper.applySort(
                wrapper, sort, w -> w.orderByDesc(User::getCreatedAt), USER_SORT_FIELDS);
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
