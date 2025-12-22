package com.only.ai.meetingroom.api.exception;

/**
 * 资源不存在异常
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}