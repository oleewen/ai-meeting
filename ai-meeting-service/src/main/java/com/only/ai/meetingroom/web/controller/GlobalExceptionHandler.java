package com.only.ai.meetingroom.web.controller;

import com.only.ai.meetingroom.api.common.response.ApiResponse;
import com.only.ai.meetingroom.application.service.AgendaGenerationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author only
 * @since 2024-01-01
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.error("INVALID_PARAM", e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ApiResponse<Void> handleIllegalStateException(IllegalStateException e) {
        return ApiResponse.error("INVALID_STATE", e.getMessage());
    }

    @ExceptionHandler(AgendaGenerationException.class)
    public ApiResponse<Void> handleAgendaGenerationException(AgendaGenerationException e) {
        return ApiResponse.error("AGENDA_GENERATION_FAILED", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("参数验证失败");
        return ApiResponse.error("INVALID_PARAM", message);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        e.printStackTrace(); // 打印异常堆栈以便调试
        return ApiResponse.error("INTERNAL_ERROR", "系统内部错误: " + e.getMessage());
    }
}

