package com.ai.pj.restcontroller;



import com.ai.pj.dto.BoardDTO;
import com.ai.pj.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;

    // 게시글 작성 처리
    @PostMapping("/post")
    public ResponseEntity<BoardDTO.Get> postBoard(
            @ModelAttribute BoardDTO.Post post, // 게시글 데이터
            @RequestParam(required = false) MultipartFile imageFile // 이미지 파일
    ) {
        try {
            String imageUrl = null;

            // 이미지 파일이 있는 경우 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = boardService.saveImage(imageFile); // 이미지 저장
            }

            // 게시글 저장
            BoardDTO.Get board = boardService.save(post, imageUrl);

            // HTTP 201 Created 반환
            return ResponseEntity.status(201).body(board);
        } catch (Exception e) {
            // 오류 발생 시 서버 오류 반환
            return ResponseEntity.status(500).build();
        }
    }


    // 게시글 수정 처리
    @PatchMapping("/{id}")
    public ResponseEntity<BoardDTO.Get> patchBoard(
            @PathVariable Long id,
            @RequestPart BoardDTO.Post updatedPost, // 게시글 데이터
            @RequestPart(required = false) MultipartFile imageFile // 이미지 파일
    ) {
        try {
            String imageUrl = null;

            // 이미지가 업로드되었을 경우 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = boardService.saveImage(imageFile);
            }

            // 게시글 수정
            BoardDTO.Get updatedBoard = boardService.update(id, updatedPost, imageUrl);

            // HTTP 200 OK와 함께 수정된 게시글 반환
            return ResponseEntity.ok(updatedBoard);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // 서버 오류 응답
        }
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<BoardDTO.Get> getBoardDetail(@PathVariable Long id){
//        BoardDTO.Get board = boardService.getBoardById(id);
//        return ResponseEntity.ok(board);
//    }

}
