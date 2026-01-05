package com.social.network.auth_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    REFRESH_TOKEN_EXPIRED(
            "REFRESH_TOKEN_INVALID",
            "Refresh token invalid",
            HttpStatus.UNAUTHORIZED
    ),
    ACCESS_TOKEN_INVALID(
            "ACCESS_TOKEN_INVALID",
            "Access token invalid",
            HttpStatus.UNAUTHORIZED
    ),
    SYSTEM_ERROR(
            "SYSTEM_ERROR",
            "System error. Please try again later",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    INVALID_CREDENTIALS(
            "INVALID_CREDENTIALS",
            "Username or password is in correct",
            HttpStatus.UNAUTHORIZED
    ), USERNAME_OR_EMAIL_ALREADY_EXISTS(
            "USERNAME_OR_EMAIL_ALREADY_EXISTS",
            "Username or email already exists",
            HttpStatus.CONFLICT
    );
    private final String code;
    private final String message;
    private final HttpStatus status;
}

