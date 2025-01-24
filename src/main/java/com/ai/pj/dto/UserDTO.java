package com.ai.pj.dto;

import com.ai.pj.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class UserDTO {
    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String id;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식을 입력하세요.")
    private String email;



    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Get {
        String id;
        String name;
        String email;
        User.UserRole role;
    }

    @Getter
    @Builder
    public static class TokenUserInfo {

        String identifier;
        String email;
        User.UserRole role;
    }

    @Getter

    public static class LoginRequest {
        String id;
        String password;
    }
}
