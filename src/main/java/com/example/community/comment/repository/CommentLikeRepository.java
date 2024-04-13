package com.example.community.comment.repository;

import com.example.community.comment.domain.Comment;
import com.example.community.comment.domain.CommentLike;
import com.example.community.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);
}
