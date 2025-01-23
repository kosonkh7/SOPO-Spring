package com.ai.pj.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum
ErrorCode {
    INVALID_JWT("INVALID_JWT", "The provided JWT is invalid."),
    EXPIRED_JWT("EXPIRED_JWT", "The provided JWT has expired."),
    USER_NOT_FOUND("USER_NOT_FOUND", "The user was not found."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An internal server error occurred."),
    JWT_TOKEN_NOT_FOUND("JWT_TOKEN_NOT_FOUND", "The JWT TOKEN is not found"),
    NOT_AUTHENTICATED_USER("NOT_AUTHENTICATED_USER", "The user not Authenticated");
    private final String code;
    private final String message;
}
