/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.service;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.user.model.dto.UserCreateDTO;
import com.atlas.system.user.model.dto.UserQueryDTO;
import com.atlas.system.user.model.dto.UserUpdateDTO;
import com.atlas.system.user.model.vo.UserListVO;
import java.util.List;

/**
 * 用户服务接口
 *
 * <p>提供用户查询和创建业务逻辑，支持根据用户ID和用户名查询用户信息，以及创建用户。
 *
 * <p>方法说明：
 *
 * <ul>
 *   <li>getUserById：根据用户ID查询用户信息
 *   <li>getUserByUsername：根据用户名查询用户信息
 *   <li>createUser：创建用户
 *   <li>assignRolesToUser：为用户批量分配角色
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface UserService {

  /**
   * 根据用户ID查询用户信息
   *
   * <p>通过用户ID查询用户的基本信息，包括用户ID、用户名、状态等。
   *
   * @param userId 用户ID
   * @return 用户信息 DTO，如果用户不存在则抛出 BusinessException
   */
  UserDTO getUserById(String userId);

  /**
   * 根据用户名查询用户信息
   *
   * <p>通过用户名查询用户的基本信息，包括用户ID、用户名、状态等。
   *
   * @param username 用户名
   * @return 用户信息 DTO，如果用户不存在则抛出 BusinessException
   */
  UserDTO getUserByUsername(String username);

  /**
   * 验证用户密码
   *
   * <p>通过用户名和密码验证用户身份，返回加密后的密码用于后续验证。
   *
   * @param username 用户名
   * @param password 明文密码
   * @return 加密后的密码，如果用户不存在或密码错误则抛出 BusinessException
   */
  String verifyPassword(String username, String password);

  /**
   * 创建用户
   *
   * <p>根据用户创建 DTO 创建新用户，密码会自动加密存储。
   *
   * @param userCreateDTO 用户创建 DTO
   * @return 用户信息 DTO
   * @throws BusinessException 如果用户名已存在，错误码：032004
   */
  UserDTO createUser(UserCreateDTO userCreateDTO);

  /**
   * 为用户批量分配角色
   *
   * <p>建立用户与角色的关联关系。若某条关联已存在则跳过（幂等）。
   *
   * @param userId 用户ID
   * @param roleIds 角色ID列表，可为空（空列表不执行任何操作）
   * @throws BusinessException 如果用户不存在或任一角色不存在，错误码：032001 或 032101
   */
  void assignRolesToUser(String userId, List<String> roleIds);

  /**
   * 更新用户
   *
   * <p>仅更新 nickname、email、phone、status、avatar；不修改 username、password。
   *
   * @param userId 用户ID
   * @param userUpdateDTO 更新 DTO
   * @return 更新后的用户信息 DTO
   * @throws BusinessException 如果用户不存在或已删除，错误码：032001
   */
  UserDTO updateUser(String userId, UserUpdateDTO userUpdateDTO);

  /**
   * 逻辑删除用户
   *
   * <p>将用户 status 置为 DELETED。
   *
   * @param userId 用户ID
   * @throws BusinessException 如果用户不存在或已删除，错误码：032001
   */
  void deleteUser(String userId);

  /**
   * 批量移除用户与角色的关联
   *
   * @param userId 用户ID
   * @param roleIds 角色ID列表，可为空（空列表不执行任何操作）
   * @throws BusinessException 如果用户不存在或任一角色不存在，错误码：032001 或 032101
   */
  void removeRolesFromUser(String userId, List<String> roleIds);

  /**
   * 分页查询用户列表
   *
   * <p>支持按用户名模糊、状态筛选，以及排序（排序字段白名单：createdAt、username，兼容 createTime/createdAt）。{@link UserQueryDTO}
   * 继承 PageQueryDTO，含 page、size、sort。
   *
   * @param query 查询条件（用户名、状态、page、size、sort）
   * @return 分页结果，列表项为 UserListVO（不包含密码）
   */
  PageResult<UserListVO> listUsersPage(UserQueryDTO query);
}
