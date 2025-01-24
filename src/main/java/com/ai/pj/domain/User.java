package com.ai.pj.domain;


import com.ai.pj.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Member")
public class User {


    /**
     * 식별자임. UUID 코드로.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String identifier;

    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Board> posts = new ArrayList<>();

    public static User createUser(UserDTO dto, PasswordEncoder passwordEncoder) {
        User user = User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
//                .password(dto.getPassword())
                .password(passwordEncoder.encode(dto.getPassword())) // 암호화해서 User 생성.
                .role(UserRole.HOLD) //  durgkf wlwjd.
                .build();
        return user;
    }

    public void setRole() {
        this.role = UserRole.ADMIN;
    }

    @Getter
    @RequiredArgsConstructor
    public static enum UserRole {
        HOLD("ROLE_NOT_REGISTERED", "회원가입 이전 사용자"),
        USER("ROLE_USER", "일반 사용자"),
        ADMIN("ROLE_ADMIN","관리자");


        private final String key;
        private final String title;
    }

}




