package com.social.network.auth_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseAppException extends RuntimeException{
    private final String errorCode;
    private final HttpStatus status;

    public BaseAppException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.status = errorCode.getStatus();
    }

}


