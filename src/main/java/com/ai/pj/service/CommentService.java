package com.ai.pj.service;

import com.ai.pj.domain.Board;
import com.ai.pj.domain.Comment;
import com.ai.pj.domain.User;
import com.ai.pj.dto.CommentDTO;
import com.ai.pj.repository.BoardRepository;
import com.ai.pj.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;

    @Transactional
    public CommentDTO.Get addComment(CommentDTO.Post commentDTO){
        Board board = boardRepository.findById(commentDTO.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다: " + commentDTO.getBoardId()));

        User user = userService.findById(commentDTO.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + commentDTO.getUserId()));

        Comment comment = Comment.builder()
                .board(board)
                .user(user)
                .content(commentDTO.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        return new CommentDTO.Get(
                savedComment.getId(),
                user.getId(),
                savedComment.getContent(),
                savedComment.getCreatedDate()
        );
    }

    @Transactional(readOnly = true)
    public List<CommentDTO.Get> getCommentsByBoard(Long boardId){
        return commentRepository.findByBoardId(boardId).stream()
                .map(comment -> new CommentDTO.Get(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getContent(),
                        comment.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId){
        commentRepository.deleteById(commentId);
    }
}
