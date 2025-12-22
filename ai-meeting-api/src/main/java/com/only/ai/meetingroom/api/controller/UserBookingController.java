package com.only.ai.meetingroom.api.controller;

import com.only.ai.meetingroom.application.dto.UserBookingListRequest;
import com.only.ai.meetingroom.application.dto.UserBookingListResponse;
import com.only.ai.meetingroom.application.service.UserApplicationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户预约查询API控制器
 * 提供个人预约列表查询等REST接口
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@RestController
@RequestMapping("/api/bookings")
public class UserBookingController {

    @Resource
    private UserApplicationService userApplicationService;

    /**
     * 获取个人预约列表
     * GET /api/bookings/my-bookings
     *
     * @param userId 用户ID（从请求头获取）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param status 预约状态筛选（可选）：ACTIVE, CANCELLED, CHECKED_IN
     * @return 个人预约列表
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status) {
        
        try {
            // 构建查询请求
            UserBookingListRequest request = new UserBookingListRequest(userId, startDate, endDate, status);
            
            // 执行查询
            UserBookingListResponse response = userApplicationService.getUserBookings(request);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 参数错误，返回400
            Map<String, String> error = new HashMap<>();
            error.put("error", "INVALID_REQUEST");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            // 服务器错误，返回500
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "服务器内部错误");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 获取今日预约
     * GET /api/bookings/today
     *
     * @param userId 用户ID（从请求头获取）
     * @return 今日预约列表
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodayBookings(@RequestHeader("X-User-Id") String userId) {
        try {
            LocalDate today = LocalDate.now();
            UserBookingListRequest request = new UserBookingListRequest(userId, today, today, null);
            
            UserBookingListResponse response = userApplicationService.getUserBookings(request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "服务器内部错误");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 获取即将到来的预约
     * GET /api/bookings/upcoming
     *
     * @param userId 用户ID（从请求头获取）
     * @param days 未来天数，默认7天
     * @return 即将到来的预约列表
     */
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingBookings(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "7") int days) {
        
        try {
            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusDays(days);
            
            UserBookingListRequest request = new UserBookingListRequest(userId, today, endDate, "ACTIVE");
            
            UserBookingListResponse response = userApplicationService.getUserBookings(request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "服务器内部错误");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 获取历史预约
     * GET /api/bookings/history
     *
     * @param userId 用户ID（从请求头获取）
     * @param days 过去天数，默认30天
     * @return 历史预约列表
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistoryBookings(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "30") int days) {
        
        try {
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusDays(days);
            
            UserBookingListRequest request = new UserBookingListRequest(userId, startDate, today.minusDays(1), null);
            
            UserBookingListResponse response = userApplicationService.getUserBookings(request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "服务器内部错误");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}