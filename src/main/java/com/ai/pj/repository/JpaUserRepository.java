package com.ai.pj.repository;

import com.ai.pj.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepositoryCustom{
    private final EntityManager em;

    @Override
    public Optional<User> findById(String id) {
        String jpql = "SELECT u from Member u where u.id=:id";
        TypedQuery<User> query = em.createQuery(jpql, User.class).setParameter("id", id);
        List<User> userList = query.getResultList();
        if(userList.size() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(userList.get(0));
    }

    @Override
    public Optional<List<User>> findByRole(User.UserRole role) {
        String jpql = "SELECT u from User where u.role=:role";
        TypedQuery<User> query = em.createQuery(jpql, User.class).setParameter("role", role);
        System.out.println("-".repeat(60));
        List<User> userList = query.getResultList();
        System.out.println("*".repeat(60));
        System.out.println(userList.toString());
        if(userList.size() == 0) {
            return Optional.empty();
        }
        System.out.println(3);
        return Optional.ofNullable(userList);
    }

//    @Override
//    @Transactional
//    public int updateByRole(String id, User.UserRole role) {
//        String jpql = "UPDATE User SET u.role=:role WHERE u.id=:id";
//        // TypedQuery는 반환값을 기대할 때 씀.
//        Query query = em.createQuery(jpql);
//        query.setParameter("role", role);
//        query.setParameter("id", id);
//        return query.executeUpdate(); // 업데이트된 행의 수 반환.
//    }


}
