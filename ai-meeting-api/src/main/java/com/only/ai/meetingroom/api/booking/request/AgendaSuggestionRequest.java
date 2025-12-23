package com.only.ai.meetingroom.api.booking.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 议程推荐请求
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class AgendaSuggestionRequest {
    /** 会议主题 */
    @NotBlank(message = "会议主题不能为空")
    private String subject;
}

