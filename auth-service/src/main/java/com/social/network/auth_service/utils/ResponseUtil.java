package com.social.network.auth_service.utils;


import com.social.network.auth_service.dto.response.BaseResponse;

import java.time.LocalDateTime;

public class ResponseUtil {

    // Response thành công
    public static <T> BaseResponse<T> success(T data, String message) {
        return BaseResponse.<T>builder()
                .status(200)
                .message(message)
                .timestamp(LocalDateTime.now())
                .errorCode(null)
                .data(data)
                .build();
    }

    // Response lỗi
    public static <T> BaseResponse<T> error(String message, String errorCode, int status) {
        return BaseResponse.<T>builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .errorCode(errorCode)
                .data(null)
                .build();
    }
}
