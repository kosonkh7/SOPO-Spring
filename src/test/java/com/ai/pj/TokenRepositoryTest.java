//package com.ai.pj;
//
//import com.ai.pj.domain.Token;
//
//import com.ai.pj.repository.TokenRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class TokenRepositoryTest {
//
//    @Autowired
//    private TokenRepository tokenRepository;
//
//    @Test
//    public void testSaveAndFindToken() {
//        // Token 객체 생성
//        Token token = new Token();
//        token.setIdentifier("user123");
//        token.setRefreshToken("refresh-token-example");
//
//        // Redis에 저장
//        tokenRepository.save(token);
//
//        // 저장된 데이터 조회
//        Token retrievedToken = tokenRepository.findById("user123").orElse(null);
//
//        assert retrievedToken != null;
//        System.out.println("Retrieved Token: " + retrievedToken.getRefreshToken());
//    }
//}
