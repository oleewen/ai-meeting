package com.only.ai.meeting.web.controller;

import com.only.ai.common.Result;
import com.only.ai.meeting.api.request.CreateBookingRequest;
import com.only.ai.meeting.api.response.BookingDTO;
import com.only.ai.meeting.application.command.CreateBookingCommand;
import com.only.ai.meeting.application.service.BookingService;
import com.only.ai.meeting.application.service.MyBookingQueryService;
import com.only.ai.meeting.domain.model.Booking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预约控制器
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Tag(name = "预约", description = "预约相关API")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private MyBookingQueryService myBookingQueryService;

    /**
     * 从Request中获取当前用户ID
     * 由AuthFilter设置
     */
    private String getCurrentUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("currentUserId");
        return userId != null ? userId : "user001"; // 默认值，用于演示
    }

    @Operation(summary = "创建预约")
    @PostMapping
    public Result<BookingDTO> createBooking(@Valid @RequestBody CreateBookingRequest request, HttpServletRequest httpRequest) {
        CreateBookingCommand command = new CreateBookingCommand();
        BeanUtils.copyProperties(request, command);
        String userId = getCurrentUserId(httpRequest);
        command.setUserId(userId);
        command.setUserName("测试用户"); // TODO: 从用户服务获取

        Booking booking = bookingService.createBooking(command);
        return Result.success(toDTO(booking));
    }

    @Operation(summary = "查看我的预定")
    @GetMapping
    public Result<List<BookingDTO>> getMyBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            HttpServletRequest httpRequest
    ) {
        String userId = getCurrentUserId(httpRequest);
        List<Booking> bookings = myBookingQueryService.queryMyBookings(userId, startDate, endDate, status);
        List<BookingDTO> dtos = bookings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return Result.success(dtos);
    }

    @Operation(summary = "查看预约详情")
    @GetMapping("/{bookingId}")
    public Result<BookingDTO> getBookingDetail(@PathVariable Long bookingId, HttpServletRequest httpRequest) {
        String userId = getCurrentUserId(httpRequest);
        Booking booking = myBookingQueryService.getBookingDetail(bookingId, userId);
        if (booking == null) {
            return Result.fail(404, "预约不存在或无权限访问");
        }
        return Result.success(toDTO(booking));
    }

    @Operation(summary = "取消预约")
    @DeleteMapping("/{bookingId}")
    public Result<Void> cancelBooking(@PathVariable Long bookingId, HttpServletRequest httpRequest) {
        String userId = getCurrentUserId(httpRequest);
        bookingService.cancelBooking(bookingId, userId);
        return Result.success(null);
    }

    @Operation(summary = "会议签到")
    @PostMapping("/{bookingId}/sign-in")
    public Result<BookingDTO> signIn(@PathVariable Long bookingId, HttpServletRequest httpRequest) {
        String userId = getCurrentUserId(httpRequest);
        bookingService.signIn(bookingId, userId);
        Booking booking = myBookingQueryService.getBookingDetail(bookingId, userId);
        return Result.success(toDTO(booking));
    }

    private BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        BeanUtils.copyProperties(booking, dto);
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
        return dto;
    }
}
