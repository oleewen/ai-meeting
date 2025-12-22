package com.only.ai.meetingroom.api.controller;

import com.only.ai.meetingroom.application.dto.MeetingRoomDetailResponse;
import com.only.ai.meetingroom.application.dto.MeetingRoomSearchRequest;
import com.only.ai.meetingroom.application.dto.MeetingRoomSearchResponse;
import com.only.ai.meetingroom.application.service.MeetingRoomApplicationService;
import com.only.ai.meetingroom.domain.model.Equipment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 会议室查询API控制器
 * 提供会议室搜索、详情查看等REST接口
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
@RestController
@RequestMapping("/api/meeting-rooms")
public class MeetingRoomController {

    @Resource
    private MeetingRoomApplicationService meetingRoomApplicationService;

    /**
     * 搜索会议室
     * GET /api/meeting-rooms/search
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param location 地点筛选（可选）
     * @param minCapacity 最小容量（可选）
     * @param equipments 设备要求，逗号分隔（可选）
     * @param sortBy 排序方式（可选）：capacity, capacity_desc, location, name
     * @return 会议室搜索结果
     */
    @GetMapping("/search")
    public ResponseEntity<MeetingRoomSearchResponse> searchRooms(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String equipments,
            @RequestParam(required = false) String sortBy) {
        
        try {
            // 解析设备要求
            Set<Equipment> requiredEquipments = null;
            if (equipments != null && !equipments.trim().isEmpty()) {
                requiredEquipments = Arrays.stream(equipments.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(Equipment::valueOf)
                    .collect(Collectors.toSet());
            }
            
            // 构建搜索请求
            MeetingRoomSearchRequest request = new MeetingRoomSearchRequest(
                startTime, endTime, location, minCapacity, requiredEquipments, sortBy);
            
            // 执行搜索
            MeetingRoomSearchResponse response = meetingRoomApplicationService.searchAvailableRooms(request);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 参数错误，返回400
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // 服务器错误，返回500
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取会议室详情
     * GET /api/meeting-rooms/{roomId}
     *
     * @param roomId 会议室ID
     * @param date 查询日期，默认为今天
     * @return 会议室详情
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<MeetingRoomDetailResponse> getRoomDetail(
            @PathVariable String roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            // 默认查询今天
            if (date == null) {
                date = LocalDate.now();
            }
            
            Optional<MeetingRoomDetailResponse> response = 
                meetingRoomApplicationService.getRoomDetail(roomId, date);
            
            if (response.isPresent()) {
                return ResponseEntity.ok(response.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取所有活跃会议室
     * GET /api/meeting-rooms
     *
     * @return 所有活跃会议室列表
     */
    @GetMapping
    public ResponseEntity<MeetingRoomSearchResponse> getAllActiveRooms() {
        try {
            java.util.List<com.only.ai.meetingroom.application.dto.MeetingRoomSearchResponse.MeetingRoomInfo> rooms = meetingRoomApplicationService.getAllActiveRooms();
            MeetingRoomSearchResponse response = new MeetingRoomSearchResponse(rooms, rooms.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}