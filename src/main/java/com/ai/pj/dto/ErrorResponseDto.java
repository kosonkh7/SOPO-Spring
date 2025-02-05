package com.ai.pj.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto {
    private int status; // HTTP 상태 코드
    private String errorMessage ;
    private LocalDateTime localDateTime;
}
