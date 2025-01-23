package com.ai.pj.mapper;


import com.ai.pj.domain.Board;
import com.ai.pj.domain.User;
import com.ai.pj.dto.BoardDTO;
import org.springframework.stereotype.Component;

@Component
// 추후에 MapStruct로 자동 매퍼로 변환.
public class BoardMapper {

    // 게시글 생성 요청 DTO를 엔티티로 변환
    public static Board PostToEntity(BoardDTO.Post post, User user) {
        return Board.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .user(user)
                .build();
    }

    // 엔티티를 조회 응답 DTO로 변환
    public static BoardDTO.Get EntityToGET(Board board) {
        return BoardDTO.Get.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUser().getId())
                .createdDate(board.getCreatedDate())
                .viewCount(board.getViewCount())
                .imageUrl(board.getImageUrl()) // 이미지 URL 추가
                .build();
    }
}
