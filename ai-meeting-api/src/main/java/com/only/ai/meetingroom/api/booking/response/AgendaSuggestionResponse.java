package com.only.ai.meetingroom.api.booking.response;

import lombok.Data;

/**
 * 议程推荐响应
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class AgendaSuggestionResponse {
    /** 生成的议程内容（Markdown格式） */
    private String agenda;
}

