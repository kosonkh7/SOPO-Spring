package com.ai.pj.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "visitCount", timeToLive = 86400) // 1일로 설정
public class VisitCount {

    @Id
    private String id;

    private String time;
    private Long count;
}
