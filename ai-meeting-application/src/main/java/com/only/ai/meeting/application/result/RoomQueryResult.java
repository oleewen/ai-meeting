package com.only.ai.meeting.application.result;

import com.only.ai.meeting.domain.model.MeetingRoom;

import java.util.List;

/**
 * 会议室查询结果
 * 
 * @author AI Meeting Team
 * @since 2025-01-27
 */
public class RoomQueryResult {
    private List<MeetingRoom> rooms;
    private int total;

    public RoomQueryResult() {
    }

    public RoomQueryResult(List<MeetingRoom> rooms) {
        this.rooms = rooms;
        this.total = rooms != null ? rooms.size() : 0;
    }

    // Getters and Setters
    public List<MeetingRoom> getRooms() {
        return rooms;
    }

    public void setRooms(List<MeetingRoom> rooms) {
        this.rooms = rooms;
        this.total = rooms != null ? rooms.size() : 0;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
