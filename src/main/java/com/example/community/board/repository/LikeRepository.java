package com.example.community.board.repository;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.Like;
import com.example.community.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberAndBoard(Member member, Board board);
}
