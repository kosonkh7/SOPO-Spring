package com.ai.pj.dto;

import lombok.*;

public class BoardDTO {


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        String title;
        String userId;
        String content;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Get {
        Long id;
        String title;
        String userId;
        String content;
    }
}
