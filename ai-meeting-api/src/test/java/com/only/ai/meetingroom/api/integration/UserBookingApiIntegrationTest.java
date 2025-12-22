package com.only.ai.meetingroom.api.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户预约API集成测试
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class UserBookingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_USER_ID = "user-002";

    /**
     * 测试获取个人预约列表 - 需要认证
     */
    @Test
    public void testGetMyBookingsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/bookings/my-bookings")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试获取个人预约列表 - 成功
     */
    @Test
    public void testGetMyBookingsSuccess() throws Exception {
        mockMvc.perform(get("/api/bookings/my-bookings")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试获取个人预约列表 - 带日期筛选
     */
    @Test
    public void testGetMyBookingsWithDateFilter() throws Exception {
        mockMvc.perform(get("/api/bookings/my-bookings")
                .header("X-User-Id", TEST_USER_ID)
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-01-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试获取个人预约列表 - 带状态筛选
     */
    @Test
    public void testGetMyBookingsWithStatusFilter() throws Exception {
        mockMvc.perform(get("/api/bookings/my-bookings")
                .header("X-User-Id", TEST_USER_ID)
                .param("status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试获取个人预约列表 - 无效状态筛选
     */
    @Test
    public void testGetMyBookingsWithInvalidStatus() throws Exception {
        mockMvc.perform(get("/api/bookings/my-bookings")
                .header("X-User-Id", TEST_USER_ID)
                .param("status", "INVALID_STATUS")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * 测试获取今日预约
     */
    @Test
    public void testGetTodayBookings() throws Exception {
        mockMvc.perform(get("/api/bookings/today")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试获取即将到来的预约
     */
    @Test
    public void testGetUpcomingBookings() throws Exception {
        mockMvc.perform(get("/api/bookings/upcoming")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试获取即将到来的预约 - 自定义天数
     */
    @Test
    public void testGetUpcomingBookingsWithCustomDays() throws Exception {
        mockMvc.perform(get("/api/bookings/upcoming")
                .header("X-User-Id", TEST_USER_ID)
                .param("days", "14")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试获取历史预约
     */
    @Test
    public void testGetHistoryBookings() throws Exception {
        mockMvc.perform(get("/api/bookings/history")
                .header("X-User-Id", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试获取历史预约 - 自定义天数
     */
    @Test
    public void testGetHistoryBookingsWithCustomDays() throws Exception {
        mockMvc.perform(get("/api/bookings/history")
                .header("X-User-Id", TEST_USER_ID)
                .param("days", "60")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());
    }

    /**
     * 测试获取今日预约 - 需要认证
     */
    @Test
    public void testGetTodayBookingsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/bookings/today")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试获取即将到来的预约 - 需要认证
     */
    @Test
    public void testGetUpcomingBookingsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/bookings/upcoming")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试获取历史预约 - 需要认证
     */
    @Test
    public void testGetHistoryBookingsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/bookings/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}