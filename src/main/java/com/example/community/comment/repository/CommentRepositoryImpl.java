package com.example.community.comment.repository;

import com.example.community.comment.domain.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.example.community.comment.domain.QComment.comment;
import static com.example.community.member.domain.QMember.member;

public class CommentRepositoryImpl implements CommentRepositoryCustom{


    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory =  new JPAQueryFactory(em);
    }


    @Override
    public List<Comment> getCommentList(Long boardId) {
        return queryFactory.selectFrom(comment)
                .join(comment.member,member).fetchJoin()
                .where(comment.board.id.eq(boardId))
                .orderBy(comment.parent.id.asc().nullsFirst()
                        ,comment.id.asc())
                .fetch();
    }
}
