package com.example.util.fixture.comment;

import com.example.community.board.domain.Board;
import com.example.community.comment.domain.Comment;
import com.example.community.member.domain.Member;

public class CommentFixture {
    public static Comment createDefaultComment(Board board, String content, Member member) {
        return createDefaultComment(board,null,content,member);
    }

    public static Comment createDefaultComment(Board board, Comment parent, String content, Member member) {
        return Comment.builder()
                .board(board)
                .parent(parent)
                .content(content)
                .member(member)
                .build();
    }
}
