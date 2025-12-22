package com.only.ai.meetingroom.application.dto;

import com.only.ai.meetingroom.domain.model.Equipment;

import java.util.List;
import java.util.Set;

/**
 * 会议室搜索响应DTO
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class MeetingRoomSearchResponse {
    
    private List<MeetingRoomInfo> rooms;
    private int totalCount;

    public MeetingRoomSearchResponse() {
    }

    public MeetingRoomSearchResponse(List<MeetingRoomInfo> rooms, int totalCount) {
        this.rooms = rooms;
        this.totalCount = totalCount;
    }

    // Getters and Setters
    public List<MeetingRoomInfo> getRooms() {
        return rooms;
    }

    public void setRooms(List<MeetingRoomInfo> rooms) {
        this.rooms = rooms;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 会议室信息内部类
     */
    public static class MeetingRoomInfo {
        private String id;
        private String name;
        private String location;
        private int capacity;
        private Set<Equipment> equipments;

        public MeetingRoomInfo() {
        }

        public MeetingRoomInfo(String id, String name, String location, int capacity, Set<Equipment> equipments) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.capacity = capacity;
            this.equipments = equipments;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public Set<Equipment> getEquipments() {
            return equipments;
        }

        public void setEquipments(Set<Equipment> equipments) {
            this.equipments = equipments;
        }

        @Override
        public String toString() {
            return "MeetingRoomInfo{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", location='" + location + '\'' +
                    ", capacity=" + capacity +
                    ", equipments=" + equipments +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MeetingRoomSearchResponse{" +
                "rooms=" + rooms +
                ", totalCount=" + totalCount +
                '}';
    }
}