package com.ai.pj.dto;

import lombok.*;

import java.time.LocalDateTime;

public class CommentDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private Long boardId;
        private String userId;
        private String content;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Get{
        private Long id;
        private String userId;
        private String content;
        private LocalDateTime createdDate;
    }
}
