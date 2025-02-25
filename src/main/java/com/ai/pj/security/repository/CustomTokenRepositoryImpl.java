package com.ai.pj.security.repository;

import com.ai.pj.domain.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
@RequiredArgsConstructor
public class CustomTokenRepositoryImpl implements CustomTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public boolean findByusable(String id) {
        System.out.println("FIND " + id);
        id = "Token:" + id;

        System.out.println("GET IN");
        // Redis의 Hash에서 "usable" 필드 가져오기
        String usable = (String) redisTemplate.opsForHash().get(id, "usable");

        if (usable != null) {
            System.out.println(usable);
        }

//
//        if (usable != null) {
//            System.out.println("USABLE" + usable);
//        }
        // 토큰이 존재하고 usable 값이 "true"면  true 반환
        return usable != null && usable.equals("use") ? true : false;
    }
}
