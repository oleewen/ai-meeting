package com.only.ai.meetingroom.api.exception;

/**
 * 认证异常
 *
 * @author ai-meeting
 * @since 2024-01-15
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}