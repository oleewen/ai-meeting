package com.only.ai.meetingroom.api.controller;

import com.only.ai.meetingroom.application.dto.BookingCreateRequest;
import com.only.ai.meetingroom.application.dto.BookingCreateResponse;
import com.only.ai.meetingroom.application.dto.BookingDetailResponse;
import com.only.ai.meetingroom.application.dto.CheckInRequest;
import com.only.ai.meetingroom.application.dto.CheckInResponse;
import com.only.ai.meetingroom.application.service.BookingApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 预约管理API控制器
 * 提供预约创建、取消、签到等REST接口
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Resource
    private BookingApplicationService bookingApplicationService;

    /**
     * 创建预约
     * POST /api/bookings
     *
     * @param request 预约创建请求
     * @param userId 用户ID（从请求头获取）
     * @return 预约创建结果
     */
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody BookingCreateRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        try {
            // 设置用户ID
            request.setUserId(userId);
            
            // 创建预约
            BookingCreateResponse response = bookingApplicationService.createBooking(request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑错误，返回400
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
     * 取消预约
     * DELETE /api/bookings/{bookingId}
     *
     * @param bookingId 预约ID
     * @param userId 用户ID（从请求头获取）
     * @return 取消结果
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(
            @PathVariable String bookingId,
            @RequestHeader("X-User-Id") String userId) {
        
        try {
            String message = bookingApplicationService.cancelBooking(bookingId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INVALID_REQUEST");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "服务器内部错误");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 签到
     * POST /api/bookings/{bookingId}/checkin
     *
     * @param bookingId 预约ID
     * @param userId 用户ID（从请求头获取）
     * @return 签到结果
     */
    @PostMapping("/{bookingId}/checkin")
    public ResponseEntity<?> checkIn(
            @PathVariable String bookingId,
            @RequestHeader("X-User-Id") String userId) {
        
        try {
            CheckInRequest request = new CheckInRequest(bookingId, userId);
            CheckInResponse response = bookingApplicationService.checkIn(request);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INVALID_REQUEST");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "服务器内部错误");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 获取预约详情
     * GET /api/bookings/{bookingId}
     *
     * @param bookingId 预约ID
     * @param userId 用户ID（从请求头获取）
     * @return 预约详情
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingDetail(
            @PathVariable String bookingId,
            @RequestHeader("X-User-Id") String userId) {
        
        try {
            Optional<BookingDetailResponse> response = 
                bookingApplicationService.getBookingDetail(bookingId, userId);
            
            if (response.isPresent()) {
                return ResponseEntity.ok(response.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INVALID_REQUEST");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "服务器内部错误");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}