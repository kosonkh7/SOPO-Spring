package com.ai.pj;

import com.ai.pj.domain.Token;
import com.ai.pj.security.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class RedisTest {
    @Autowired
    TokenRepository tokenRepository;

    @Test
    public void testRedisConnection() {

//        Token token = new Token("");
//        tokenRepository.save(token);
//        Optional<Token> value = tokenRepository.findById("testKey");
//
//        System.out.println(value.get().getId());
//        System.out.println(value.get().getRefreshToken());
    }

}
