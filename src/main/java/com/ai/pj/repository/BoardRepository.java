package com.ai.pj.repository;

import com.ai.pj.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);

    List<Board> findAllByOrderByCreatedDateDesc(); // 최신순 정렬
}
