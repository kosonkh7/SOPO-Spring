package com.ai.pj.repository;

import com.ai.pj.domain.IpLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpLogRepository extends CrudRepository<IpLog, String> {
}
