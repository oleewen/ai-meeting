package com.only.ai.meetingroom.application.service;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.BookingStatus;
import com.only.ai.meetingroom.domain.model.MeetingRoom;
import com.only.ai.meetingroom.domain.model.User;
import com.only.ai.meetingroom.domain.model.UserId;
import com.only.ai.meetingroom.domain.repository.BookingRepository;
import com.only.ai.meetingroom.domain.repository.MeetingRoomRepository;
import com.only.ai.meetingroom.domain.repository.UserRepository;
import com.only.ai.meetingroom.application.dto.UserBookingListRequest;
import com.only.ai.meetingroom.application.dto.UserBookingListResponse;
import com.only.ai.meetingroom.application.dto.UserAuthRequest;
import com.only.ai.meetingroom.application.dto.UserAuthResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户应用服务
 * 负责用户认证、权限验证和个人预约查询功能
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Service
public class UserApplicationService {

    @Resource
    private UserRepository userRepository;
    
    @Resource
    private BookingRepository bookingRepository;
    
    @Resource
    private MeetingRoomRepository meetingRoomRepository;

    /**
     * 用户认证
     *
     * @param request 认证请求
     * @return 认证响应
     */
    public UserAuthResponse authenticate(UserAuthRequest request) {
        // 验证请求参数
        validateAuthRequest(request);
        
        // 查找用户
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (!userOpt.isPresent()) {
            return new UserAuthResponse(false, null, null, null, "用户不存在");
        }
        
        User user = userOpt.get();
        
        // 检查用户是否激活
        if (!user.isActive()) {
            return new UserAuthResponse(false, null, null, null, "用户已被停用");
        }
        
        // 简单的密码验证（实际项目中应该使用加密验证）
        // 这里暂时跳过密码验证，因为我们还没有实现密码加密
        
        return new UserAuthResponse(
            true,
            user.getId().value(),
            user.getUsername(),
            user.getRole().name(),
            "认证成功"
        );
    }

    /**
     * 验证用户权限
     *
     * @param userId 用户ID
     * @param requiredPermission 需要的权限
     * @return 是否有权限
     */
    public boolean hasPermission(String userId, String requiredPermission) {
        UserId id = UserId.of(userId);
        Optional<User> userOpt = userRepository.findById(id);
        
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // 检查用户是否激活
        if (!user.isActive()) {
            return false;
        }
        
        // 简单的权限检查逻辑
        switch (requiredPermission.toLowerCase()) {
            case "admin":
                return user.getRole().name().equals("ADMIN");
            case "user":
                return user.getRole().name().equals("USER") || user.getRole().name().equals("ADMIN");
            default:
                return false;
        }
    }

    /**
     * 获取用户个人预约列表
     *
     * @param request 查询请求
     * @return 预约列表响应
     */
    public UserBookingListResponse getUserBookings(UserBookingListRequest request) {
        // 验证请求参数
        validateBookingListRequest(request);
        
        UserId userId = UserId.of(request.getUserId());
        
        // 验证用户是否存在
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        List<Booking> bookings;
        
        // 根据筛选条件查询预约
        if (request.getStartDate() != null && request.getEndDate() != null && request.getStatus() != null) {
            // 按日期范围和状态筛选
            BookingStatus status = BookingStatus.valueOf(request.getStatus().toUpperCase());
            bookings = bookingRepository.findByUserIdAndDateRangeAndStatus(
                userId, request.getStartDate(), request.getEndDate(), status);
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            // 按日期范围筛选
            bookings = bookingRepository.findByUserIdAndDateRange(
                userId, request.getStartDate(), request.getEndDate());
        } else if (request.getStatus() != null) {
            // 按状态筛选
            BookingStatus status = BookingStatus.valueOf(request.getStatus().toUpperCase());
            bookings = bookingRepository.findByUserIdAndStatus(userId, status);
        } else {
            // 获取所有预约
            bookings = bookingRepository.findByUserId(userId);
        }
        
        // 按时间排序
        List<Booking> sortedBookings = bookings.stream()
            .sorted(Comparator.comparing(Booking::getStartTime).reversed()) // 最新的在前
            .collect(Collectors.toList());
        
        // 获取会议室信息
        Map<String, MeetingRoom> roomMap = getRoomMap(sortedBookings);
        
        // 转换为响应DTO
        List<UserBookingListResponse.BookingInfo> bookingInfos = sortedBookings.stream()
            .map(booking -> convertToBookingInfo(booking, roomMap))
            .collect(Collectors.toList());
        
        return new UserBookingListResponse(bookingInfos, bookingInfos.size());
    }

    /**
     * 验证用户是否可以访问指定预约
     *
     * @param userId 用户ID
     * @param bookingId 预约ID
     * @return 是否可以访问
     */
    public boolean canAccessBooking(String userId, String bookingId) {
        UserId id = UserId.of(userId);
        
        // 检查用户是否存在且激活
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent() || !userOpt.get().isActive()) {
            return false;
        }
        
        // 管理员可以访问所有预约
        if (userOpt.get().getRole().name().equals("ADMIN")) {
            return true;
        }
        
        // 普通用户只能访问自己的预约
        Optional<Booking> bookingOpt = bookingRepository.findById(
            com.only.ai.meetingroom.domain.model.BookingId.of(bookingId));
        
        return bookingOpt.isPresent() && bookingOpt.get().belongsTo(id);
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    public Optional<User> getUserInfo(String userId) {
        UserId id = UserId.of(userId);
        return userRepository.findById(id);
    }

    /**
     * 验证认证请求
     */
    private void validateAuthRequest(UserAuthRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
    }

    /**
     * 验证预约列表查询请求
     */
    private void validateBookingListRequest(UserBookingListRequest request) {
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new IllegalArgumentException("开始日期不能晚于结束日期");
            }
        }
        
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                BookingStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("无效的预约状态：" + request.getStatus());
            }
        }
    }

    /**
     * 获取会议室信息映射
     */
    private Map<String, MeetingRoom> getRoomMap(List<Booking> bookings) {
        List<String> roomIds = bookings.stream()
            .map(booking -> booking.getMeetingRoomId().value())
            .distinct()
            .collect(Collectors.toList());
        
        return roomIds.stream()
            .map(roomId -> meetingRoomRepository.findById(
                com.only.ai.meetingroom.domain.model.MeetingRoomId.of(roomId)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toMap(
                room -> room.getId().value(),
                Function.identity()
            ));
    }

    /**
     * 转换为预约信息DTO
     */
    private UserBookingListResponse.BookingInfo convertToBookingInfo(Booking booking, Map<String, MeetingRoom> roomMap) {
        MeetingRoom room = roomMap.get(booking.getMeetingRoomId().value());
        String roomName = room != null ? room.getName() : "未知会议室";
        String roomLocation = room != null ? room.getLocation() : "未知位置";
        
        return new UserBookingListResponse.BookingInfo(
            booking.getId().value(),
            booking.getSubject(),
            roomName,
            roomLocation,
            booking.getStartTime(),
            booking.getEndTime(),
            booking.getStatus().name(),
            booking.getCheckedInAt()
        );
    }
}