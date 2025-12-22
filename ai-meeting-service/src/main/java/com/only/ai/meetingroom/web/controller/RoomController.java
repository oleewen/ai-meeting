package com.only.ai.meetingroom.web.controller;

import com.only.ai.meetingroom.api.common.response.ApiResponse;
import com.only.ai.meetingroom.api.room.request.RoomQueryRequest;
import com.only.ai.meetingroom.api.room.response.RoomDTO;
import com.only.ai.meetingroom.application.service.RoomQueryApplicationService;
import com.only.ai.meetingroom.domain.model.Room;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议室控制器
 *
 * @author only
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomQueryApplicationService roomQueryApplicationService;

    public RoomController(RoomQueryApplicationService roomQueryApplicationService) {
        this.roomQueryApplicationService = roomQueryApplicationService;
    }

    /**
     * 查询可用会议室
     */
    @PostMapping("/query")
    public ApiResponse<List<RoomDTO>> queryAvailableRooms(@RequestBody RoomQueryRequest request) {
        List<Room> rooms = roomQueryApplicationService.queryAvailableRooms(
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getLocation(),
                request.getMinCapacity(),
                request.getEquipment()
        );

        List<RoomDTO> roomDTOs = rooms.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ApiResponse.success(roomDTOs);
    }

    /**
     * 获取会议室详情
     */
    @GetMapping("/{roomId}")
    public ApiResponse<RoomDTO> getRoomDetail(@PathVariable Long roomId) {
        Room room = roomQueryApplicationService.getRoomDetail(new Room.RoomId(roomId));
        return ApiResponse.success(toDTO(room));
    }

    private RoomDTO toDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId().value());
        dto.setName(room.getName());
        dto.setLocation(room.getLocation());
        dto.setCapacity(room.getCapacity());
        dto.setEquipment(room.getEquipment());
        dto.setStatus(room.getStatus().name());
        return dto;
    }
}

