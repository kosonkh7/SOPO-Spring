package com.ai.pj.security.repository;

import com.ai.pj.domain.VisitCount;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface VisitCountRepository extends KeyValueRepository<VisitCount, String>, CustomVisitCountRepository{
}
