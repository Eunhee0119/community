package com.example.community.board.repository;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.dto.BoardDto;
import com.example.community.board.domain.dto.BoardSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BoardRepositoryCustom {
    Optional<Board> findByIdWhitMember(Long boardId);

    Page<BoardDto> getSearchBoardList(Pageable page, BoardSearchDto boardSearch);
}
