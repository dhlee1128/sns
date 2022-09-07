package com.project.sns.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "User Name is duplicated"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"), 
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Password is invalid"),
    
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
    ;

    private HttpStatus status;
    private String message;

}