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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final UserService userService;
    // Post 요청
    public BoardDTO.Get save(BoardDTO.Post post) {
        String userId = post.getUserId();

        // 로그인을 하고 들어온다는 가정이기에 상관없지만, 참조되는 ID가 없을 경우. exception 발생
        User user = userService.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("no User Here"+ userId));

        System.out.println(user.getId()) ;

        // 오류나면 여기 풀어줘도 됨.
        return boardMapper.EntityToGET(boardRepository.save(boardMapper.PostToEntity(post, user)));
    }

    @Transactional
    public BoardDTO.Get update(Long id, BoardDTO.Post updatedPost) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다: " + id));

        board.setTitle(updatedPost.getTitle());
        board.setContent(updatedPost.getContent());

        return boardMapper.EntityToGET(boardRepository.save(board));
    }

    public Boolean delete(Long reqNum) {

        try {
            boardRepository.deleteById(reqNum);
            return true;
        } catch (EmptyResultDataAccessException e ) {
            return false;
        }
        // 우선 직접 삭제로 설계.
    }

    @Transactional(readOnly = true)
    public List<BoardDTO.Get> getAllBoards() {
        // DB에서 모든 게시판 데이터 조회
        return boardRepository.findAll().stream()
                .map(board -> new BoardDTO.Get(
                        board.getId(),
                        board.getTitle(),
                        board.getUser().getId(),
                        board.getContent(),
                        board.getViewCount(), // 조회수 추가
                        board.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public BoardDTO.Get getBoardById(Long id){
        // 게시글 데이터 가져오기
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다: " + id));
        // 조회수 증가
        boardRepository.incrementViewCount(id);

        return BoardMapper.EntityToGET(board);
    }
}
