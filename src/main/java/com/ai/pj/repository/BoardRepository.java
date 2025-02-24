package com.ai.pj.repository;

import com.ai.pj.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);

    List<Board> findAllByOrderByCreatedDateDesc(); // 최신순 정렬
    Page<Board> findAllByOrderByCreatedDateDesc(Pageable pageable);  // 최신순 정렬하여 페이징 처리

    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% ORDER BY b.createdDate DESC")
    Page<Board> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.content LIKE %:keyword% ORDER BY b.createdDate DESC")
    Page<Board> searchByContent(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) ORDER BY b.createdDate DESC")
    Page<Board> searchByTitleAndContent(@Param("keyword") String keyword, Pageable pageable);
    // b.user.id -> User 도메인의 id로 검색한다는 것.
    @Query("SELECT b FROM Board b WHERE b.user.id LIKE %:keyword% ORDER BY b.createdDate DESC")
    Page<Board> searchByUserId(@Param("keyword") String keyword, Pageable pageable);
}
