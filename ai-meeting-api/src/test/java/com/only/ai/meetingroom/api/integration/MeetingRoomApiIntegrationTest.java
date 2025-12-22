package com.only.ai.meetingroom.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.only.ai.meetingroom.application.dto.MeetingRoomSearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 会议室API集成测试
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class MeetingRoomApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试获取所有活跃会议室
     */
    @Test
    public void testGetAllActiveRooms() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/meeting-rooms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        MeetingRoomSearchResponse response = objectMapper.readValue(responseContent, MeetingRoomSearchResponse.class);
        
        assertNotNull(response);
        assertNotNull(response.getRooms());
        assertTrue(response.getTotalCount() >= 0);
    }

    /**
     * 测试搜索会议室 - 无参数
     */
    @Test
    public void testSearchRoomsWithoutParams() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/search")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试搜索会议室 - 带地点筛选
     */
    @Test
    public void testSearchRoomsWithLocation() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/search")
                .param("location", "1楼东区")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试搜索会议室 - 带容量筛选
     */
    @Test
    public void testSearchRoomsWithCapacity() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/search")
                .param("minCapacity", "8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试搜索会议室 - 带设备筛选
     */
    @Test
    public void testSearchRoomsWithEquipments() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/search")
                .param("equipments", "PROJECTOR,WHITEBOARD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试搜索会议室 - 带排序
     */
    @Test
    public void testSearchRoomsWithSorting() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/search")
                .param("sortBy", "capacity")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试搜索会议室 - 带时间段
     */
    @Test
    public void testSearchRoomsWithTimeSlot() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/search")
                .param("startTime", "2024-01-20T09:00:00")
                .param("endTime", "2024-01-20T10:00:00")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试获取会议室详情 - 存在的会议室
     */
    @Test
    public void testGetRoomDetailExists() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/room-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("room-001"))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.location").exists())
                .andExpect(jsonPath("$.capacity").isNumber())
                .andExpect(jsonPath("$.timeSlots").isArray());
    }

    /**
     * 测试获取会议室详情 - 不存在的会议室
     */
    @Test
    public void testGetRoomDetailNotExists() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/non-existent-room")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * 测试获取会议室详情 - 带日期参数
     */
    @Test
    public void testGetRoomDetailWithDate() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/room-001")
                .param("date", "2024-01-20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value("2024-01-20"));
    }

    /**
     * 测试搜索会议室 - 无效设备参数
     */
    @Test
    public void testSearchRoomsWithInvalidEquipment() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/search")
                .param("equipments", "INVALID_EQUIPMENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试搜索会议室 - 无效容量参数
     */
    @Test
    public void testSearchRoomsWithInvalidCapacity() throws Exception {
        mockMvc.perform(get("/api/meeting-rooms/search")
                .param("minCapacity", "-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}