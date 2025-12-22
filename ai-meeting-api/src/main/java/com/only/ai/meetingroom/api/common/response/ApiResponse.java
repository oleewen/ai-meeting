package com.only.ai.meetingroom.api.common.response;

import lombok.Data;

/**
 * 统一API响应
 *
 * @author only
 * @since 2024-01-01
 */
@Data
public class ApiResponse<T> {
    /** 是否成功 */
    private Boolean success;
    /** 错误码 */
    private String code;
    /** 错误消息 */
    private String message;
    /** 数据 */
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}

