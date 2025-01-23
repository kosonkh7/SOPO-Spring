package com.ai.pj.repository;


import com.ai.pj.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom{

    @Query("UPDATE User u SET u.role=:role WHERE u.id=:id")
    @Modifying
    @Transactional
    void updateByRole(@Param("id") String id, @Param("role") User.UserRole role);
}
