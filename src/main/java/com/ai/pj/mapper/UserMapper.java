package com.ai.pj.mapper;


import com.ai.pj.domain.User;
import com.ai.pj.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // 엔티티를 조회 DTO로 변환,
    public UserDTO.Get EntityToGET(User user) {
        return UserDTO.Get.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public UserDTO.TokenUserInfo EntityToTokenUserInfo(User user) {
        return UserDTO.TokenUserInfo.builder()
                .identifier(user.getIdentifier())
                .userid(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
