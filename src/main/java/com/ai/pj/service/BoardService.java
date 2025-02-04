package com.ai.pj.service;


import com.ai.pj.domain.Board;
import com.ai.pj.domain.User;
import com.ai.pj.dto.BoardDTO;
import com.ai.pj.mapper.BoardMapper;
import com.ai.pj.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final UserService userService;

    // 이미지 저장 처리
    public String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null; // 이미지가 없는 경우
        }

        // 실제 파일 저장 경로
        String uploadDir = "src/main/resources/static/uploads/";
        String originalFilename = imageFile.getOriginalFilename();
        String fileName = URLEncoder.encode(System.currentTimeMillis() + "_" + originalFilename, StandardCharsets.UTF_8);
        Path filePath = Paths.get(uploadDir, fileName);

        // 디렉토리가 없으면 생성
        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        Files.write(filePath, imageFile.getBytes());

        // 클라이언트에서 접근 가능한 URL 반환
        return "/uploads/" + fileName;
    }

    // 게시글 저장
    @Transactional
    public BoardDTO.Get save(BoardDTO.Post post, String imageUrl) {
        String userId = post.getUserId();

        // 유효한 사용자 검증
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("no User Here" + userId));

        Board board = Board.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .user(user)
                .imageUrl(imageUrl) // 이미지 URL 설정
                .build();

        return boardMapper.EntityToGET(boardRepository.save(board));
    }

    // 게시글 수정
    @Transactional
    public BoardDTO.Get update(Long id, BoardDTO.Post updatedPost, String imageUrl) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다: " + id));

        board.setTitle(updatedPost.getTitle());
        board.setContent(updatedPost.getContent());

        // 새로운 이미지가 업로드된 경우 업데이트
        if (imageUrl != null) {
            board.setImageUrl(imageUrl);
        }

        return boardMapper.EntityToGET(boardRepository.save(board));
    }

    // 게시글 삭제
    public void delete(Long id) {
        if (!boardRepository.existsById(id)){
            throw new EntityNotFoundException("게시글을 찾을 수 없습니다. " + id);
        }
        boardRepository.deleteById(id);
    }

    // 게시글 목록 조회
    @Transactional(readOnly = false)
    public List<BoardDTO.Get> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(board -> new BoardDTO.Get(
                        board.getId(),
                        board.getTitle(),
                        board.getUser().getId(),
                        board.getContent(),
                        board.getViewCount(), // 조회수 추가
                        board.getCreatedDate(),
                        board.getImageUrl() // 이미지 URL 추가
                ))
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    @Transactional(readOnly = false)
    public BoardDTO.Get getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다: " + id));

        // 조회수 증가
        boardRepository.incrementViewCount(id);

        return boardMapper.EntityToGET(board);
    }
}
