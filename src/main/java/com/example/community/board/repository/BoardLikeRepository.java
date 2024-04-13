package com.example.community.board.repository;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.BoardLike;
import com.example.community.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByMemberAndBoard(Member member, Board board);
}
