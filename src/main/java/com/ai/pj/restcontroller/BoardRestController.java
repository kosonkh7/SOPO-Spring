package com.ai.pj.restcontroller;



import com.ai.pj.dto.BoardDTO;
import com.ai.pj.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;

    // Write

    // 나중에 json으로 오니까 바꿔야될 걸?
    @PostMapping("/post")
    public ResponseEntity<BoardDTO.Get> postBoard (@ModelAttribute BoardDTO.Post post) {
        BoardDTO.Get board = boardService.save(post);

        // HTTP 201 Created와 함께 저장된 게시물 정보를 반환
        return ResponseEntity
                .status(201) // HTTP 201 Created
                .body(board); // 저장된 게시물 정보를 포함
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BoardDTO.Get> patchBoard(
            @PathVariable Long id,
            @RequestBody BoardDTO.Post updatedPost
    ){
        BoardDTO.Get updatedBoard = boardService.update(id, updatedPost);
        // 수정된 게시글 반환
        return ResponseEntity.ok(updatedBoard);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<BoardDTO.Get> getBoardDetail(@PathVariable Long id){
//        BoardDTO.Get board = boardService.getBoardById(id);
//        return ResponseEntity.ok(board);
//    }

}
