package com.ai.pj.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("IpLog")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IpLog implements Serializable {

    @Id
    private String ip;

    private String role;

    private Long count;

    private LocalDateTime recordTime;

    @TimeToLive // TTL 설정 (초 단위)
    private Long expiration;
}
