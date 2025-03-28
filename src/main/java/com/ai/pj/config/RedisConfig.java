package com.ai.pj.config;

import com.ai.pj.domain.Token;
import com.ai.pj.domain.VisitCount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration(enforceUniqueMethods = false)
@EnableCaching
public class RedisConfig {
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 여기다가 setPort , host
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setPort(port);
        config.setHostName(host);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate_token() {
        // redisTemplate를 받아와서 set, get, delete를 사용
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        // setKeySerializer, setValueSerializer 설정
        // redis-cli를 통해 직접 데이터를 조회 시 알아볼 수 없는 형태로 출력되는 것을 방지
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate_str_token() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 🔹 Key는 String
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 🔹 Value는 String (이전에는 JSON 직렬화됨)
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // 🔹 HashKey, HashValue도 String으로 설정
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer()); // ✅ 여기 수정!

        return redisTemplate;
    }




    @Bean
    public RedisTemplate<String, VisitCount> redisTemplate_visitCount() {
        RedisTemplate<String, VisitCount> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // (De)Serialization 설정 (JSON 형식 저장)
        Jackson2JsonRedisSerializer<VisitCount> serializer = new Jackson2JsonRedisSerializer<>(VisitCount.class);
        template.setDefaultSerializer(serializer);
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());

        return template;
    }
}