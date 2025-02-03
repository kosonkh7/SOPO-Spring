package com.ai.pj.security.repository;

import com.ai.pj.domain.VisitCount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class CustomVisitCountRepositoryImpl implements CustomVisitCountRepository{

    private final RedisTemplate<String, VisitCount> redisTemplate;
    @Override
    public List<VisitCount> findBid(String id) {
        id = "visitCount:" + id;
        System.out.println();
        Set<String> keys = redisTemplate.keys(id + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        System.out.println(keys.toString());

        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(visitCount -> visitCount != null)
                .collect(Collectors.toList());
    }
}
