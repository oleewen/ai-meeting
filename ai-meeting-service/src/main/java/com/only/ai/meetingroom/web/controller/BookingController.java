package com.only.ai.meetingroom.web.controller;

import com.only.ai.meetingroom.api.booking.request.AgendaSuggestionRequest;
import com.only.ai.meetingroom.api.booking.request.CreateBookingRequest;
import com.only.ai.meetingroom.api.booking.response.AgendaSuggestionResponse;
import com.only.ai.meetingroom.api.booking.response.BookingDTO;
import com.only.ai.meetingroom.api.common.response.ApiResponse;
import com.only.ai.meetingroom.application.service.AgendaGenerationApplicationService;
import com.only.ai.meetingroom.application.service.AgendaGenerationException;
import com.only.ai.meetingroom.application.service.BookingCreateApplicationService;
import com.only.ai.meetingroom.application.service.BookingManagementApplicationService;
import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.Room;
import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.domain.repository.RoomRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预约控制器
 *
 * @author only
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingCreateApplicationService bookingCreateApplicationService;
    private final BookingManagementApplicationService bookingManagementApplicationService;
    private final AgendaGenerationApplicationService agendaGenerationApplicationService;
    private final RoomRepository roomRepository;

    public BookingController(BookingCreateApplicationService bookingCreateApplicationService,
                             BookingManagementApplicationService bookingManagementApplicationService,
                             AgendaGenerationApplicationService agendaGenerationApplicationService,
                             RoomRepository roomRepository) {
        this.bookingCreateApplicationService = bookingCreateApplicationService;
        this.bookingManagementApplicationService = bookingManagementApplicationService;
        this.agendaGenerationApplicationService = agendaGenerationApplicationService;
        this.roomRepository = roomRepository;
    }

    /**
     * 创建预约
     */
    @PostMapping
    public ApiResponse<BookingDTO> createBooking(@RequestBody CreateBookingRequest request, HttpSession session) {
        Long userId = getUserId(session);
        Booking booking = bookingCreateApplicationService.createBooking(
                new Room.RoomId(request.getRoomId()),
                new User.UserId(userId),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getSubject(),
                request.getAttendeeCount(),
                request.getRemark()
        );
        return ApiResponse.success(toDTO(booking));
    }

    /**
     * 获取我的预约列表
     */
    @GetMapping("/my")
    public ApiResponse<List<BookingDTO>> getMyBookings(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String status,
            HttpSession session) {
        Long userId = getUserId(session);
        List<Booking> bookings;

        if (startDate != null && endDate != null) {
            bookings = bookingManagementApplicationService.getUserBookingsByDateRange(
                    new User.UserId(userId), startDate, endDate);
        } else {
            bookings = bookingManagementApplicationService.getUserBookings(new User.UserId(userId));
        }

        // 状态筛选
        if (status != null && !status.isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> b.getStatus().name().equals(status))
                    .collect(Collectors.toList());
        }

        List<BookingDTO> bookingDTOs = bookings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ApiResponse.success(bookingDTOs);
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{bookingId}")
    public ApiResponse<BookingDTO> getBookingDetail(@PathVariable Long bookingId) {
        Booking booking = bookingManagementApplicationService.getBookingDetail(new Booking.BookingId(bookingId));
        return ApiResponse.success(toDTO(booking));
    }

    /**
     * 取消预约
     */
    @PostMapping("/{bookingId}/cancel")
    public ApiResponse<BookingDTO> cancelBooking(@PathVariable Long bookingId, HttpSession session) {
        Long userId = getUserId(session);
        Booking booking = bookingManagementApplicationService.cancelBooking(
                new Booking.BookingId(bookingId),
                new User.UserId(userId)
        );
        return ApiResponse.success(toDTO(booking));
    }

    /**
     * 签到
     */
    @PostMapping("/{bookingId}/checkin")
    public ApiResponse<BookingDTO> checkIn(@PathVariable Long bookingId, HttpSession session) {
        Long userId = getUserId(session);
        Booking booking = bookingManagementApplicationService.checkIn(
                new Booking.BookingId(bookingId),
                new User.UserId(userId)
        );
        return ApiResponse.success(toDTO(booking));
    }

    /**
     * 生成会议议程推荐
     */
    @PostMapping("/agenda-suggestion")
    public ApiResponse<AgendaSuggestionResponse> generateAgendaSuggestion(
            @Valid @RequestBody AgendaSuggestionRequest request,
            HttpSession session) {
        // 验证用户已登录
        getUserId(session);

        try {
            String agenda = agendaGenerationApplicationService.generateAgenda(request.getSubject());
            AgendaSuggestionResponse response = new AgendaSuggestionResponse();
            response.setAgenda(agenda);
            return ApiResponse.success(response);
        } catch (AgendaGenerationException e) {
            // 返回友好的错误提示
            return ApiResponse.error("AGENDA_GENERATION_FAILED", e.getMessage());
        }
    }

    private Long getUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("未登录");
        }
        return userId;
    }

    private BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId().value());
        dto.setRoomId(booking.getRoomId().value());
        // 获取会议室名称
        roomRepository.findById(booking.getRoomId()).ifPresent(room -> {
            dto.setRoomName(room.getName());
        });
        dto.setUserId(booking.getUserId().value());
        dto.setDate(booking.getDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setSubject(booking.getSubject());
        dto.setAttendeeCount(booking.getAttendeeCount());
        dto.setRemark(booking.getRemark());
        dto.setStatus(booking.getStatus().name());
        dto.setCreateTime(booking.getCreateTime());
        return dto;
    }
}

