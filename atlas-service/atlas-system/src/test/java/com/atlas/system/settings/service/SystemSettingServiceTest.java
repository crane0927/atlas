/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.settings.mapper.SystemSettingMapper;
import com.atlas.system.settings.model.dto.SystemSettingCreateDTO;
import com.atlas.system.settings.model.dto.SystemSettingQueryDTO;
import com.atlas.system.settings.model.dto.SystemSettingUpdateDTO;
import com.atlas.system.settings.model.entity.SystemSetting;
import com.atlas.system.settings.model.enums.SystemSettingType;
import com.atlas.system.settings.model.vo.SystemSettingVO;
import com.atlas.system.settings.service.impl.SystemSettingServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SystemSettingService 单元测试
 *
 * <p>测试系统设置服务的业务逻辑。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class SystemSettingServiceTest {

  @Mock private SystemSettingMapper systemSettingMapper;

  @InjectMocks private SystemSettingServiceImpl systemSettingService;

  private SystemSetting systemSetting;
  private SystemSetting customSetting;

  @BeforeEach
  void setUp() {
    systemSetting = new SystemSetting();
    systemSetting.setSettingId(1L);
    systemSetting.setKey("site.name");
    systemSetting.setValue("Atlas");
    systemSetting.setType(SystemSettingType.SYSTEM);

    customSetting = new SystemSetting();
    customSetting.setSettingId(2L);
    customSetting.setKey("theme.color");
    customSetting.setValue("blue");
    customSetting.setType(SystemSettingType.CUSTOM);
  }

  @Test
  void testListSettingsSuccess() {
    SystemSettingQueryDTO queryDTO = new SystemSettingQueryDTO();
    queryDTO.setType(SystemSettingType.SYSTEM);
    queryDTO.setKeyword("site");

    when(systemSettingMapper.selectList(any())).thenReturn(Arrays.asList(systemSetting));

    List<SystemSettingVO> result = systemSettingService.listSettings(queryDTO);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("site.name", result.get(0).getKey());
    assertEquals(SystemSettingType.SYSTEM, result.get(0).getType());

    verify(systemSettingMapper).selectList(any());
  }

  @Test
  void testCreateSettingSuccess() {
    SystemSettingCreateDTO createDTO = new SystemSettingCreateDTO();
    createDTO.setKey("theme.color");
    createDTO.setValue("blue");

    when(systemSettingMapper.selectOne(any())).thenReturn(null);
    when(systemSettingMapper.insert(any(SystemSetting.class)))
        .thenAnswer(
            invocation -> {
              SystemSetting entity = invocation.getArgument(0);
              entity.setSettingId(2L);
              return 1;
            });

    SystemSettingVO result = systemSettingService.createSetting(createDTO);

    assertNotNull(result);
    assertEquals("theme.color", result.getKey());
    assertEquals("blue", result.getValue());
    assertEquals(SystemSettingType.CUSTOM, result.getType());

    verify(systemSettingMapper).selectOne(any());
    verify(systemSettingMapper).insert(any(SystemSetting.class));
  }

  @Test
  void testCreateSettingWithDuplicateKey() {
    SystemSettingCreateDTO createDTO = new SystemSettingCreateDTO();
    createDTO.setKey("site.name");
    createDTO.setValue("Atlas");

    when(systemSettingMapper.selectOne(any())).thenReturn(systemSetting);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> systemSettingService.createSetting(createDTO));

    assertEquals(SystemErrorCode.SYSTEM_SETTING_KEY_ALREADY_EXISTS, exception.getErrorCode());
    assertEquals("设置项 key 已存在", exception.getMessage());

    verify(systemSettingMapper).selectOne(any());
    verify(systemSettingMapper, never()).insert(any(SystemSetting.class));
  }

  @Test
  void testUpdateSettingValueNotFound() {
    SystemSettingUpdateDTO updateDTO = new SystemSettingUpdateDTO();
    updateDTO.setValue("Atlas Pro");

    when(systemSettingMapper.selectOne(any())).thenReturn(null);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> systemSettingService.updateSettingValue("site.name", updateDTO));

    assertEquals(SystemErrorCode.SYSTEM_SETTING_NOT_FOUND, exception.getErrorCode());
    assertEquals("设置项不存在", exception.getMessage());
  }

  @Test
  void testDeleteCustomSettingForbidden() {
    when(systemSettingMapper.selectOne(any())).thenReturn(systemSetting);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> systemSettingService.deleteCustomSetting("site.name"));

    assertEquals(SystemErrorCode.SYSTEM_SETTING_SYSTEM_DELETE_FORBIDDEN, exception.getErrorCode());
    assertEquals("系统默认设置不可删除", exception.getMessage());
  }

  @Test
  void testDeleteCustomSettingSuccess() {
    when(systemSettingMapper.selectOne(any())).thenReturn(customSetting);
    when(systemSettingMapper.deleteById(2L)).thenReturn(1);

    systemSettingService.deleteCustomSetting("theme.color");

    verify(systemSettingMapper).deleteById(2L);
  }
}
