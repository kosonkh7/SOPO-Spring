package com.ai.pj.security.repository;

import com.ai.pj.domain.Token;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, String> {
}
