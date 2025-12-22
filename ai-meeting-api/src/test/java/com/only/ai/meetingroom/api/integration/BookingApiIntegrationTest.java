package com.only.ai.meetingroom.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.only.ai.meetingroom.application.dto.BookingCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 预约API集成测试
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class BookingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_USER_ID = "user-002";

    /**
     * 测试创建预约 - 成功场景
     */
    @Test
    public void testCreateBookingSuccess() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setMeetingRoomId("room-001");
        request.setSubject("测试会议");
        request.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
        request.setEndTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0));
        request.setAttendeeCount(5);
        request.setNotes("这是一个测试会议");

        mockMvc.perform(post("/api/bookings")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.subject").value("测试会议"))
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * 测试创建预约 - 缺少用户ID
     */
    @Test
    public void testCreateBookingWithoutUserId() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setMeetingRoomId("room-001");
        request.setSubject("测试会议");
        request.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        request.setEndTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0));
        request.setAttendeeCount(5);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试创建预约 - 无效请求数据
     */
    @Test
    public void testCreateBookingWithInvalidData() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        // 缺少必填字段

        mockMvc.perform(post("/api/bookings")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * 测试创建预约 - 过去时间
     */
    @Test
    public void testCreateBookingWithPastTime() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setMeetingRoomId("room-001");
        request.setSubject("测试会议");
        request.setStartTime(LocalDateTime.now().minusHours(1)); // 过去时间
        request.setEndTime(LocalDateTime.now().plusHours(1));
        request.setAttendeeCount(5);

        mockMvc.perform(post("/api/bookings")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * 测试获取预约详情 - 需要认证
     */
    @Test
    public void testGetBookingDetailWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/bookings/test-booking-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试获取预约详情 - 不存在的预约
     */
    @Test
    public void testGetBookingDetailNotExists() throws Exception {
        mockMvc.perform(get("/api/bookings/non-existent-booking")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * 测试取消预约 - 需要认证
     */
    @Test
    public void testCancelBookingWithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/bookings/test-booking-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试取消预约 - 不存在的预约
     */
    @Test
    public void testCancelBookingNotExists() throws Exception {
        mockMvc.perform(delete("/api/bookings/non-existent-booking")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * 测试签到 - 需要认证
     */
    @Test
    public void testCheckInWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/bookings/test-booking-id/checkin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试签到 - 不存在的预约
     */
    @Test
    public void testCheckInNotExists() throws Exception {
        mockMvc.perform(post("/api/bookings/non-existent-booking/checkin")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }
}