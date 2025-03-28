package com.ai.pj.security.repository;

public interface CustomTokenRepository {
    boolean findByusable(String id);
}
