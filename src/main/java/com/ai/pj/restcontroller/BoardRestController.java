package com.ai.pj.restcontroller;



import com.ai.pj.dto.BoardDTO;
import com.ai.pj.dto.CommentDTO;
import com.ai.pj.service.BoardService;
import com.ai.pj.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;
    private final CommentService commentService;

    // --------------------------------------------------
    // [1] 기존에 있던 "게시글 작성 처리" (REST 방식)
    // --------------------------------------------------
    @PostMapping("/post")
    public ResponseEntity<BoardDTO.Get> postBoard(
            @ModelAttribute BoardDTO.Post post, // multipart/form-data 형태로 받음
            @RequestParam(required = false) MultipartFile imageFile
    ) {
        try {
            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = boardService.saveImage(imageFile);
            }

            BoardDTO.Get board = boardService.save(post, imageUrl);
            Long boardId = board.getId();

            // 원본 코드에 맞춰 302 Found + Location 헤더(리다이렉트)
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create("/board/" + boardId))
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // --------------------------------------------------
    // [2] 기존에 있던 "게시글 수정 처리" (REST 방식)
    // --------------------------------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<BoardDTO.Get> patchBoard(
            @PathVariable Long id,
            @RequestPart BoardDTO.Post updatedPost, // multipart/form-data
            @RequestPart(required = false) MultipartFile imageFile
    ) {
        try {
            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = boardService.saveImage(imageFile);
            }

            BoardDTO.Get updatedBoard = boardService.update(id, updatedPost, imageUrl);
            return ResponseEntity.ok(updatedBoard);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // --------------------------------------------------
    // [3] 게시글 목록 조회 (REST)
    //     - 같은 "/board/" 경로라도,
    //       "text/html" vs "application/json" 으로 응답 구분 가능
    // --------------------------------------------------
    @GetMapping(
            value = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<BoardDTO.Get>> getBoardListJson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            List<BoardDTO.Get> boardList = boardService.getAllBoards(page, size).getContent();
            return ResponseEntity.ok(boardList);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // --------------------------------------------------
    // [4] 게시글 상세 조회 (REST)
    //     - GET "/board/{id}" (JSON)
    // --------------------------------------------------
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BoardDTO.Get> getBoardDetailJson(@PathVariable Long id) {
        try {
            BoardDTO.Get board = boardService.getBoardById(id);
            return ResponseEntity.ok(board);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // --------------------------------------------------
    // [5] 게시글 삭제 (REST)
    //     - 같은 "/board/delete/{id}" 경로지만, HTTP 메서드가 다름 (DELETE vs POST)
    // --------------------------------------------------
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBoardRest(@PathVariable Long id) {
        try {
            boardService.delete(id);
            return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // --------------------------------------------------
    // (Optional) 댓글 목록 조회, 댓글 작성 등
    // 역시 "/board/{id}/comments" 등으로 추가 가능
    // --------------------------------------------------
    @GetMapping(
            value = "/{id}/comments",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<CommentDTO.Get>> getCommentsJson(@PathVariable Long id) {
        try {
            List<CommentDTO.Get> comments = commentService.getCommentsByBoard(id);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}