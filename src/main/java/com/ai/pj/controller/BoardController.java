package com.ai.pj.controller;


import com.ai.pj.dto.BoardDTO;
import com.ai.pj.dto.CommentDTO;
import com.ai.pj.security.details.CustomUserDetails;
import com.ai.pj.service.BoardService;
import com.ai.pj.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    // /board 요청 → /board/ 로 리다이렉트
    @GetMapping
    public String redirectToSlash() {
        return "redirect:/board/";
    }

    // 게시판 메인 페이지 (목록 조회, HTML)
    @GetMapping("/")
    public String reqBoard(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size, // 페이지네이션
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String searchType,  // 검색
                           Model model,
                           Authentication authentication) {

        // 인증 정보에서 사용자 정보 추출
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("userId", customUserDetails.getTokenUserInfo().getIdentifier());

        Page<BoardDTO.Get> boardPage;
        // 검색어와 검색옵션이 둘 다 존재하면 검색, 아니면 전체 목록
        if (keyword != null && !keyword.trim().isEmpty() &&
                searchType != null && !searchType.trim().isEmpty()) {
            // BoardService에서 검색 타입에 따라 분기
            boardPage = boardService.search(searchType, keyword.trim(), page, size);
        } else {
            // 검색어 없으면 전체 목록
            boardPage = boardService.getAllBoards(page, size);
        }

        model.addAttribute("boardList", boardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", boardPage.getTotalPages());
        model.addAttribute("prevPage", page > 0 ? page - 1 : 0);
        model.addAttribute("nextPage", page < boardPage.getTotalPages() - 1 ? page + 1 : boardPage.getTotalPages() - 1);
        model.addAttribute("prevDisabled", page == 0);
        model.addAttribute("nextDisabled", page == boardPage.getTotalPages() - 1);

        // 검색어와 옵션을 유지하기 위해 모델에 넣어둠
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "/board/board"; // HTML 템플릿
    }

    // 게시글 작성 페이지 (HTML)
    @GetMapping("/write")
    public String reqWrite() {
        return "/board/write";
    }

    // 게시글 작성 처리 (웹 요청, 폼 전송)
    @PostMapping("/write")
    public String writeBoard(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(required = false) MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) {
        try {
            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = boardService.saveImage(imageFile);
            }

            BoardDTO.Post post = new BoardDTO.Post();
            post.setTitle(title);
            post.setContent(content);
            post.setUserId("testUser"); // 실제 환경에선 인증에서 추출

            boardService.save(post, imageUrl);
            redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
            return "redirect:/board/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시글 작성 중 오류가 발생했습니다.");
            return "redirect:/board/write";
        }
    }

    // 게시글 상세 페이지 (HTML)
    @GetMapping("/{id}")
    public String getBoardDetail(@PathVariable Long id, Model model) {
        BoardDTO.Get board = boardService.getBoardById(id);
        List<CommentDTO.Get> comments = commentService.getCommentsByBoard(id);

        model.addAttribute("board", board);
        model.addAttribute("comments", comments);

        return "/board/detail";
    }

    // 게시글 삭제 (웹 요청, 폼 전송)
    @PostMapping("/delete/{id}")
    public String deleteBoard(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            boardService.delete(id);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "해당 게시글을 찾을 수 없습니다.");
        }
        return "redirect:/board/";
    }

    // 게시글 수정 페이지 (HTML)
    @GetMapping("/edit/{id}")
    public String editBoard(@PathVariable Long id, Model model) {
        BoardDTO.Get board = boardService.getBoardById(id);
        model.addAttribute("board", board);
        return "/board/edit";
    }

    // 게시글 수정 처리 (웹 요청, 폼 전송)
    @PostMapping("/edit/{id}")
    public String updateBoard(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String content,
                              @RequestParam(required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            String imageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = boardService.saveImage(imageFile);
            }

            BoardDTO.Post updatedPost = new BoardDTO.Post();
            updatedPost.setTitle(title);
            updatedPost.setContent(content);
            updatedPost.setUserId("testUser"); // 실제 환경에선 인증에서 추출

            boardService.update(id, updatedPost, imageUrl);
            redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");

            return "redirect:/board/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시글 수정 중 오류가 발생했습니다.");
            return "redirect:/board/edit/" + id;
        }
    }
}