package com.ai.pj.repository;

import com.ai.pj.domain.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenRepository extends CrudRepository<Token, String> {
}
