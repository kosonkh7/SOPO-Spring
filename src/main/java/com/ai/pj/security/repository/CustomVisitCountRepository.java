package com.ai.pj.security.repository;

import com.ai.pj.domain.VisitCount;

import java.util.List;

public interface CustomVisitCountRepository {
    List<VisitCount> findBid(String date);
}
