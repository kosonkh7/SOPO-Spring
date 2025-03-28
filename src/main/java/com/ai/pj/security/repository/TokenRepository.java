package com.ai.pj.security.repository;

import com.ai.pj.domain.Token;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface TokenRepository extends KeyValueRepository<Token, String>, CustomTokenRepository {
}
