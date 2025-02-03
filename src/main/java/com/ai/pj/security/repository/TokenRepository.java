package com.ai.pj.security.repository;

import com.ai.pj.domain.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface TokenRepository extends CrudRepository<Token, String> {
}
