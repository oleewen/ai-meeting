package com.only.ai.meetingroom.api.room.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 会议室查询请求
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class RoomQueryRequest {
    /** 日期 */
    private LocalDate date;
    /** 开始时间 */
    private LocalTime startTime;
    /** 结束时间 */
    private LocalTime endTime;
    /** 地点筛选（可选） */
    private String location;
    /** 最小容量（可选） */
    private Integer minCapacity;
    /** 设备筛选（可选） */
    private List<String> equipment;
}

