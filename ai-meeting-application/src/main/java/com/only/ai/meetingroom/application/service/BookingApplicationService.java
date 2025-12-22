package com.only.ai.meetingroom.application.service;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.BookingId;
import com.only.ai.meetingroom.domain.model.BookingStatus;
import com.only.ai.meetingroom.domain.model.MeetingRoom;
import com.only.ai.meetingroom.domain.model.MeetingRoomId;
import com.only.ai.meetingroom.domain.model.TimeSlot;
import com.only.ai.meetingroom.domain.model.UserId;
import com.only.ai.meetingroom.domain.repository.BookingRepository;
import com.only.ai.meetingroom.domain.repository.MeetingRoomRepository;
import com.only.ai.meetingroom.domain.service.BookingDomainService;
import com.only.ai.meetingroom.application.dto.BookingCreateRequest;
import com.only.ai.meetingroom.application.dto.BookingCreateResponse;
import com.only.ai.meetingroom.application.dto.BookingDetailResponse;
import com.only.ai.meetingroom.application.dto.CheckInRequest;
import com.only.ai.meetingroom.application.dto.CheckInResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 预约应用服务
 * 负责协调预约相关的业务逻辑，包括创建、取消、签到功能
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Service
public class BookingApplicationService {

    @Resource
    private BookingRepository bookingRepository;
    
    @Resource
    private MeetingRoomRepository meetingRoomRepository;
    
    @Resource
    private BookingDomainService bookingDomainService;

    /**
     * 创建预约
     *
     * @param request 预约创建请求
     * @return 预约创建响应
     */
    public BookingCreateResponse createBooking(BookingCreateRequest request) {
        // 验证请求参数
        validateCreateRequest(request);
        
        MeetingRoomId meetingRoomId = MeetingRoomId.of(request.getMeetingRoomId());
        UserId userId = UserId.of(request.getUserId());
        TimeSlot timeSlot = TimeSlot.of(request.getStartTime(), request.getEndTime());
        
        // 验证会议室是否存在且可用
        Optional<MeetingRoom> roomOpt = meetingRoomRepository.findById(meetingRoomId);
        if (!roomOpt.isPresent()) {
            throw new IllegalArgumentException("会议室不存在");
        }
        
        MeetingRoom room = roomOpt.get();
        if (!room.isActive()) {
            throw new IllegalArgumentException("会议室已停用");
        }
        
        // 验证容量是否满足要求
        if (!room.canAccommodate(request.getAttendeeCount())) {
            throw new IllegalArgumentException("会议室容量不足，最大容量：" + room.getCapacity());
        }
        
        // 验证业务规则
        bookingDomainService.validateBookingRules(timeSlot);
        
        // 检查时间冲突
        if (bookingDomainService.hasTimeConflict(meetingRoomId, timeSlot)) {
            List<Booking> conflictingBookings = bookingDomainService.getConflictingBookings(meetingRoomId, timeSlot);
            String conflictInfo = buildConflictInfo(conflictingBookings);
            throw new IllegalArgumentException("时间冲突：" + conflictInfo);
        }
        
        // 创建预约
        BookingId bookingId = BookingId.of(UUID.randomUUID().toString());
        Booking booking = new Booking(
            bookingId,
            meetingRoomId,
            userId,
            request.getSubject(),
            request.getStartTime(),
            request.getEndTime(),
            request.getAttendeeCount(),
            request.getNotes()
        );
        
        // 保存预约
        bookingRepository.save(booking);
        
        return new BookingCreateResponse(
            booking.getId().value(),
            booking.getSubject(),
            room.getName(),
            room.getLocation(),
            booking.getStartTime(),
            booking.getEndTime(),
            booking.getStatus().name(),
            "预约创建成功"
        );
    }

    /**
     * 取消预约
     *
     * @param bookingId 预约ID
     * @param userId 用户ID
     * @return 取消结果信息
     */
    public String cancelBooking(String bookingId, String userId) {
        BookingId id = BookingId.of(bookingId);
        UserId currentUserId = UserId.of(userId);
        
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("预约不存在");
        }
        
        Booking booking = bookingOpt.get();
        
        // 验证权限
        if (!booking.belongsTo(currentUserId)) {
            throw new IllegalArgumentException("无权限取消此预约");
        }
        
        // 验证是否可以取消
        if (!bookingDomainService.canCancelBooking(booking, LocalDateTime.now())) {
            throw new IllegalArgumentException("预约开始前2小时内不能取消");
        }
        
        // 取消预约
        booking.cancel();
        bookingRepository.save(booking);
        
        return "预约已成功取消";
    }

    /**
     * 签到
     *
     * @param request 签到请求
     * @return 签到响应
     */
    public CheckInResponse checkIn(CheckInRequest request) {
        BookingId bookingId = BookingId.of(request.getBookingId());
        UserId userId = UserId.of(request.getUserId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            throw new IllegalArgumentException("预约不存在");
        }
        
        Booking booking = bookingOpt.get();
        
        // 验证权限
        if (!booking.belongsTo(userId)) {
            throw new IllegalArgumentException("无权限为此预约签到");
        }
        
        // 验证签到时间窗口
        LocalDateTime checkInTime = LocalDateTime.now();
        if (!bookingDomainService.canCheckInAt(booking, checkInTime)) {
            String windowDesc = bookingDomainService.getCheckInWindowDescription();
            throw new IllegalArgumentException("不在签到时间窗口内。" + windowDesc);
        }
        
        // 执行签到
        booking.checkIn();
        bookingRepository.save(booking);
        
        return new CheckInResponse(
            booking.getId().value(),
            booking.getSubject(),
            booking.getCheckedInAt(),
            "签到成功"
        );
    }

    /**
     * 获取预约详情
     *
     * @param bookingId 预约ID
     * @param userId 用户ID
     * @return 预约详情
     */
    public Optional<BookingDetailResponse> getBookingDetail(String bookingId, String userId) {
        BookingId id = BookingId.of(bookingId);
        UserId currentUserId = UserId.of(userId);
        
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (!bookingOpt.isPresent()) {
            return Optional.empty();
        }
        
        Booking booking = bookingOpt.get();
        
        // 验证权限
        if (!booking.belongsTo(currentUserId)) {
            throw new IllegalArgumentException("无权限查看此预约");
        }
        
        // 获取会议室信息
        Optional<MeetingRoom> roomOpt = meetingRoomRepository.findById(booking.getMeetingRoomId());
        String roomName = roomOpt.map(MeetingRoom::getName).orElse("未知会议室");
        String roomLocation = roomOpt.map(MeetingRoom::getLocation).orElse("未知位置");
        
        // 计算可用操作
        LocalDateTime now = LocalDateTime.now();
        boolean canCancel = bookingDomainService.canCancelBooking(booking, now);
        boolean canCheckIn = bookingDomainService.canCheckInAt(booking, now);
        
        BookingDetailResponse response = new BookingDetailResponse(
            booking.getId().value(),
            booking.getSubject(),
            roomName,
            roomLocation,
            booking.getStartTime(),
            booking.getEndTime(),
            booking.getAttendeeCount(),
            booking.getNotes(),
            booking.getStatus().name(),
            booking.getCheckedInAt(),
            canCancel,
            canCheckIn
        );
        
        return Optional.of(response);
    }

    /**
     * 验证创建预约请求
     */
    private void validateCreateRequest(BookingCreateRequest request) {
        if (request.getMeetingRoomId() == null || request.getMeetingRoomId().trim().isEmpty()) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }
        
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("会议主题不能为空");
        }
        
        if (request.getStartTime() == null) {
            throw new IllegalArgumentException("开始时间不能为空");
        }
        
        if (request.getEndTime() == null) {
            throw new IllegalArgumentException("结束时间不能为空");
        }
        
        if (request.getStartTime().isAfter(request.getEndTime()) || 
            request.getStartTime().isEqual(request.getEndTime())) {
            throw new IllegalArgumentException("开始时间必须早于结束时间");
        }
        
        if (request.getAttendeeCount() <= 0) {
            throw new IllegalArgumentException("参会人数必须大于0");
        }
    }

    /**
     * 构建冲突信息
     */
    private String buildConflictInfo(List<Booking> conflictingBookings) {
        if (conflictingBookings.isEmpty()) {
            return "存在时间冲突";
        }
        
        Booking firstConflict = conflictingBookings.get(0);
        return String.format("与现有预约冲突：%s (%s - %s)", 
            firstConflict.getSubject(),
            firstConflict.getStartTime().toString(),
            firstConflict.getEndTime().toString()
        );
    }
}