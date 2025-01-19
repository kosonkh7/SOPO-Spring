package com.ai.pj.restcontroller;

import com.ai.pj.dto.CommentDTO;
import com.ai.pj.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO.Get> addComment(@RequestBody CommentDTO.Post commentDTO) {
        CommentDTO.Get createdComment = commentService.addComment(commentDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdComment);
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentDTO.Get>> getCommentsByBoard(@PathVariable Long boardId) {
        List<CommentDTO.Get> comments = commentService.getCommentsByBoard(boardId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity
                .noContent()
                .build();
    }

}
