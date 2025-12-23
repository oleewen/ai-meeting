package com.only.ai.meetingroom.infrastructure.call;

/**
 * DeepSeek大模型API异常
 *
 * @author only
 * @since 2024-01-01
 */
public class DeepSeekApiException extends Exception {
    public DeepSeekApiException(String message) {
        super(message);
    }

    public DeepSeekApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

