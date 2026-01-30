/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.infra.web.exception.GlobalExceptionHandler;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.settings.model.dto.SystemSettingUpdateDTO;
import com.atlas.system.settings.model.enums.SystemSettingType;
import com.atlas.system.settings.model.vo.SystemSettingVO;
import com.atlas.system.settings.service.SystemSettingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * SystemSettingController 集成测试
 *
 * <p>测试系统设置控制器的接口功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@WebMvcTest(
    controllers = SystemSettingController.class,
    excludeAutoConfiguration = {
      org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    })
@Import(GlobalExceptionHandler.class)
@TestPropertySource(
    properties = {
      "spring.autoconfigure.exclude=com.alibaba.cloud.nacos.NacosConfigAutoConfiguration,com.alibaba.cloud.nacos.NacosDiscoveryAutoConfiguration,com.alibaba.cloud.nacos.endpoint.NacosConfigEndpointAutoConfiguration",
      "spring.cloud.nacos.config.enabled=false",
      "spring.cloud.nacos.discovery.enabled=false"
    })
class SystemSettingControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private SystemSettingService systemSettingService;

  @Test
  void testListSystemSettingsSuccess() throws Exception {
    SystemSettingVO vo = new SystemSettingVO();
    vo.setKey("site.name");
    vo.setValue("Atlas");
    vo.setType(SystemSettingType.SYSTEM);

    when(systemSettingService.listSettings(any())).thenReturn(Collections.singletonList(vo));

    mockMvc
        .perform(get("/api/v1/system-settings").param("type", "SYSTEM").param("keyword", "site"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data[0].key").value("site.name"))
        .andExpect(jsonPath("$.data[0].value").value("Atlas"))
        .andExpect(jsonPath("$.data[0].type").value("SYSTEM"));
  }

  @Test
  void testUpdateSystemSettingSuccess() throws Exception {
    SystemSettingUpdateDTO updateDTO = new SystemSettingUpdateDTO();
    updateDTO.setValue("Atlas Pro");

    SystemSettingVO vo = new SystemSettingVO();
    vo.setKey("site.name");
    vo.setValue("Atlas Pro");
    vo.setType(SystemSettingType.SYSTEM);

    when(systemSettingService.updateSettingValue(any(), any())).thenReturn(vo);

    mockMvc
        .perform(
            put("/api/v1/system-settings/site.name")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data.key").value("site.name"))
        .andExpect(jsonPath("$.data.value").value("Atlas Pro"))
        .andExpect(jsonPath("$.data.type").value("SYSTEM"));
  }

  @Test
  void testUpdateSystemSettingNotFound() throws Exception {
    SystemSettingUpdateDTO updateDTO = new SystemSettingUpdateDTO();
    updateDTO.setValue("Atlas Pro");

    when(systemSettingService.updateSettingValue(any(), any()))
        .thenThrow(new BusinessException(SystemErrorCode.SYSTEM_SETTING_NOT_FOUND, "设置项不存在"));

    mockMvc
        .perform(
            put("/api/v1/system-settings/site.name")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(SystemErrorCode.SYSTEM_SETTING_NOT_FOUND))
        .andExpect(jsonPath("$.message").value("设置项不存在"));
  }
}
