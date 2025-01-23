package com.ai.pj.dto;

import com.ai.pj.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private T data; // 응답 데이터
    private HttpStatus status; // HTTP 상태 코드
    private String message; // 메시지 (선택적)
    private Map<String, String> errors; // 에러 상세 정보.
    private User users;
    private int updateRows;



    public ResponseDTO(Map<String,String> errorMap, HttpStatus status) {
        this.errors = errorMap;
        this.status = status;
    }

    public ResponseDTO(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public ResponseDTO(User user, HttpStatus status) {
        this.users = user;
        this.status = status;
    }

    public ResponseDTO(int updateRows, HttpStatus status) {
        this.updateRows = updateRows;
        this.status = status;
    }
}
