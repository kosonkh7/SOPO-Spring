package com.ai.pj.mapper;


import com.ai.pj.domain.Board;
import com.ai.pj.domain.User;
import com.ai.pj.dto.BoardDTO;
import org.springframework.stereotype.Component;


@Component
// 추후에 Mapstruct로 자동 매퍼로 변환.
public class BoardMapper {
    public static Board PostToEntity(BoardDTO.Post post, User user) {
        return Board.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .user(user)
                .build();
    }

    public static BoardDTO.Get EntityToGET(Board board){
        return BoardDTO.Get.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUser().getId())
                .createdDate(board.getCreatedDate())
                .viewCount(board.getViewCount())
                .build();
    }
}
