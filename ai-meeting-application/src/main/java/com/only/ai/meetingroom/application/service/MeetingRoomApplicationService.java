package com.only.ai.meetingroom.application.service;

import com.only.ai.meetingroom.domain.model.Booking;
import com.only.ai.meetingroom.domain.model.Equipment;
import com.only.ai.meetingroom.domain.model.MeetingRoom;
import com.only.ai.meetingroom.domain.model.MeetingRoomId;
import com.only.ai.meetingroom.domain.model.TimeSlot;
import com.only.ai.meetingroom.domain.repository.BookingRepository;
import com.only.ai.meetingroom.domain.repository.MeetingRoomRepository;
import com.only.ai.meetingroom.application.dto.MeetingRoomSearchRequest;
import com.only.ai.meetingroom.application.dto.MeetingRoomSearchResponse;
import com.only.ai.meetingroom.application.dto.MeetingRoomDetailResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 会议室应用服务
 * 负责协调会议室相关的业务逻辑，包括查询、筛选和排序功能
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@Service
public class MeetingRoomApplicationService {

    @Resource
    private MeetingRoomRepository meetingRoomRepository;
    
    @Resource
    private BookingRepository bookingRepository;

    /**
     * 搜索可用会议室
     * 根据时间段、地点、容量和设备要求筛选会议室
     *
     * @param request 搜索请求参数
     * @return 符合条件的会议室列表
     */
    public MeetingRoomSearchResponse searchAvailableRooms(MeetingRoomSearchRequest request) {
        // 验证请求参数
        validateSearchRequest(request);
        
        List<MeetingRoom> availableRooms;
        
        if (request.getStartTime() != null && request.getEndTime() != null) {
            // 查询指定时间段内可用的会议室
            TimeSlot timeSlot = TimeSlot.of(request.getStartTime(), request.getEndTime());
            availableRooms = meetingRoomRepository.findAvailableByFilters(
                timeSlot, 
                request.getLocation(), 
                request.getMinCapacity(), 
                request.getRequiredEquipments()
            );
        } else {
            // 查询所有符合筛选条件的会议室
            availableRooms = meetingRoomRepository.findByFilters(
                request.getLocation(), 
                request.getMinCapacity(), 
                request.getRequiredEquipments()
            );
        }
        
        // 应用排序
        List<MeetingRoom> sortedRooms = applySorting(availableRooms, request.getSortBy());
        
        // 转换为响应DTO
        List<MeetingRoomSearchResponse.MeetingRoomInfo> roomInfos = sortedRooms.stream()
            .map(this::convertToRoomInfo)
            .collect(Collectors.toList());
            
        return new MeetingRoomSearchResponse(roomInfos, roomInfos.size());
    }

    /**
     * 获取会议室详细信息
     * 包括会议室基本信息和当日时间轴排期
     *
     * @param roomId 会议室ID
     * @param date 查询日期
     * @return 会议室详细信息
     */
    public Optional<MeetingRoomDetailResponse> getRoomDetail(String roomId, LocalDate date) {
        MeetingRoomId meetingRoomId = MeetingRoomId.of(roomId);
        
        Optional<MeetingRoom> roomOpt = meetingRoomRepository.findById(meetingRoomId);
        if (!roomOpt.isPresent()) {
            return Optional.empty();
        }
        
        MeetingRoom room = roomOpt.get();
        
        // 获取当日的预约信息
        List<Booking> activeBookings = bookingRepository.findActiveBookingsByRoomAndDate(meetingRoomId, date);
        
        // 构建时间轴信息
        List<MeetingRoomDetailResponse.TimeSlotInfo> timeSlots = activeBookings.stream()
            .map(booking -> new MeetingRoomDetailResponse.TimeSlotInfo(
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getSubject(),
                booking.getStatus().name()
            ))
            .sorted(Comparator.comparing(MeetingRoomDetailResponse.TimeSlotInfo::getStartTime))
            .collect(Collectors.toList());
        
        MeetingRoomDetailResponse response = new MeetingRoomDetailResponse(
            room.getId().value(),
            room.getName(),
            room.getLocation(),
            room.getCapacity(),
            room.getEquipments(),
            date,
            timeSlots
        );
        
        return Optional.of(response);
    }

    /**
     * 获取所有活跃的会议室
     *
     * @return 所有活跃会议室列表
     */
    public List<MeetingRoomSearchResponse.MeetingRoomInfo> getAllActiveRooms() {
        List<MeetingRoom> activeRooms = meetingRoomRepository.findAllActive();
        
        return activeRooms.stream()
            .map(this::convertToRoomInfo)
            .collect(Collectors.toList());
    }

    /**
     * 验证搜索请求参数
     */
    private void validateSearchRequest(MeetingRoomSearchRequest request) {
        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (request.getStartTime().isAfter(request.getEndTime()) || 
                request.getStartTime().isEqual(request.getEndTime())) {
                throw new IllegalArgumentException("开始时间必须早于结束时间");
            }
            
            if (request.getStartTime().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("不能查询过去的时间");
            }
        }
        
        if (request.getMinCapacity() != null && request.getMinCapacity() <= 0) {
            throw new IllegalArgumentException("最小容量必须大于0");
        }
    }

    /**
     * 应用排序规则
     */
    private List<MeetingRoom> applySorting(List<MeetingRoom> rooms, String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return rooms; // 不排序
        }
        
        switch (sortBy.toLowerCase()) {
            case "capacity":
                return rooms.stream()
                    .sorted(Comparator.comparing(MeetingRoom::getCapacity))
                    .collect(Collectors.toList());
            case "capacity_desc":
                return rooms.stream()
                    .sorted(Comparator.comparing(MeetingRoom::getCapacity).reversed())
                    .collect(Collectors.toList());
            case "location":
                return rooms.stream()
                    .sorted(Comparator.comparing(MeetingRoom::getLocation))
                    .collect(Collectors.toList());
            case "name":
                return rooms.stream()
                    .sorted(Comparator.comparing(MeetingRoom::getName))
                    .collect(Collectors.toList());
            default:
                return rooms; // 不支持的排序方式，返回原列表
        }
    }

    /**
     * 转换为会议室信息DTO
     */
    private MeetingRoomSearchResponse.MeetingRoomInfo convertToRoomInfo(MeetingRoom room) {
        return new MeetingRoomSearchResponse.MeetingRoomInfo(
            room.getId().value(),
            room.getName(),
            room.getLocation(),
            room.getCapacity(),
            room.getEquipments()
        );
    }
}