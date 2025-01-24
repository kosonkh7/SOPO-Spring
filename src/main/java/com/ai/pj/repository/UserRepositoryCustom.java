package com.ai.pj.repository;


import com.ai.pj.domain.User;

import java.util.List;
import java.util.Optional;


public interface UserRepositoryCustom {
    Optional<User> findUserById(String id);
    Optional<List<User>> findByRole(User.UserRole role);
}
