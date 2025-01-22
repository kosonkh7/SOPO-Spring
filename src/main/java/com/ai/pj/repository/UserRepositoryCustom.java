package com.ai.pj.repository;


import com.ai.pj.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepositoryCustom {
    Optional<User> findById(String id);
    Optional<List<User>> findByRole(User.UserRole role);
}
