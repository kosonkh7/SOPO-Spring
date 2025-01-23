package com.ai.pj.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@RedisHash("Token")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Token implements Serializable {

    @Id
    private String identifier;

    private String refreshToken;
}
