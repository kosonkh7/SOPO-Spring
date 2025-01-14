package com.ai.pj.repository;


import com.ai.pj.domain.User;

import java.util.Optional;


public interface UserRepositoryCustom {
    Optional<User> findById(String id);
}
