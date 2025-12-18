package com.only.ai.meeting.web.controller;

import com.only.ai.common.Result;
import com.only.ai.meeting.api.request.QueryRoomsRequest;
import com.only.ai.meeting.api.response.MeetingRoomDTO;
import com.only.ai.meeting.application.query.RoomQuery;
import com.only.ai.meeting.application.result.RoomQueryResult;
import com.only.ai.meeting.application.service.MeetingRoomQueryService;
import com.only.ai.meeting.domain.model.Booking;
import com.only.ai.meeting.domain.model.MeetingRoom;
import com.only.ai.meeting.domain.repository.BookingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议室控制器
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
@Tag(name = "会议室", description = "会议室相关API")
@RestController
@RequestMapping("/api/meeting-rooms")
public class MeetingRoomController {

    @Autowired
    private MeetingRoomQueryService meetingRoomQueryService;

    @Autowired
    private BookingRepository bookingRepository;

    @Operation(summary = "查询可用会议室")
    @GetMapping
    public Result<List<MeetingRoomDTO>> queryAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) List<String> equipment
    ) {
        RoomQuery query = new RoomQuery(date, startTime, endTime);
        query.setLocation(location);
        query.setMinCapacity(minCapacity);
        query.setEquipment(equipment);

        RoomQueryResult result = meetingRoomQueryService.queryAvailableRooms(query);
        List<MeetingRoomDTO> dtos = result.getRooms().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return Result.success(dtos);
    }

    @Operation(summary = "查看会议室当日排期")
    @GetMapping("/{roomId}/schedule")
    public Result<RoomScheduleDTO> getRoomSchedule(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        MeetingRoom room = meetingRoomQueryService.findById(roomId);
        if (room == null) {
            return Result.fail(404, "会议室不存在");
        }

        List<Booking> bookings = bookingRepository.findByRoomIdAndDate(roomId, date);
        
        RoomScheduleDTO schedule = new RoomScheduleDTO();
        schedule.setRoomId(roomId);
        schedule.setRoomName(room.getName());
        schedule.setDate(date);
        schedule.setTimeSlots(buildTimeSlots(bookings));

        return Result.success(schedule);
    }

    private MeetingRoomDTO toDTO(MeetingRoom room) {
        MeetingRoomDTO dto = new MeetingRoomDTO();
        BeanUtils.copyProperties(room, dto);
        dto.setStatus(room.getStatus() != null ? room.getStatus().name() : null);
        return dto;
    }

    private List<TimeSlotDTO> buildTimeSlots(List<Booking> bookings) {
        // 简化实现：返回当日的预约时间段
        List<TimeSlotDTO> slots = new ArrayList<>();
        for (Booking booking : bookings) {
            TimeSlotDTO slot = new TimeSlotDTO();
            slot.setStartTime(booking.getStartTime());
            slot.setEndTime(booking.getEndTime());
            slot.setStatus("OCCUPIED");
            slot.setBookingId(booking.getId());
            slot.setSubject(booking.getSubject());
            slots.add(slot);
        }
        return slots;
    }

    // 内部DTO类
    public static class RoomScheduleDTO {
        private Long roomId;
        private String roomName;
        private LocalDate date;
        private List<TimeSlotDTO> timeSlots;

        // Getters and Setters
        public Long getRoomId() {
            return roomId;
        }

        public void setRoomId(Long roomId) {
            this.roomId = roomId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public List<TimeSlotDTO> getTimeSlots() {
            return timeSlots;
        }

        public void setTimeSlots(List<TimeSlotDTO> timeSlots) {
            this.timeSlots = timeSlots;
        }
    }

    public static class TimeSlotDTO {
        private LocalDate date;
        private java.time.LocalTime startTime;
        private java.time.LocalTime endTime;
        private String status;
        private Long bookingId;
        private String subject;

        // Getters and Setters
        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public java.time.LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(java.time.LocalTime startTime) {
            this.startTime = startTime;
        }

        public java.time.LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(java.time.LocalTime endTime) {
            this.endTime = endTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getBookingId() {
            return bookingId;
        }

        public void setBookingId(Long bookingId) {
            this.bookingId = bookingId;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }
}
