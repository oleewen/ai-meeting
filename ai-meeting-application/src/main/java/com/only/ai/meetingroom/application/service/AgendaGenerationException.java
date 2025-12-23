package com.only.ai.meetingroom.application.service;

/**
 * 议程生成异常
 *
 * @author only
 * @since 2024-01-01
 */
public class AgendaGenerationException extends Exception {
    public AgendaGenerationException(String message) {
        super(message);
    }

    public AgendaGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

