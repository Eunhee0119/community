package com.example.community.comment.service.request;

import com.example.community.board.domain.Board;
import com.example.community.comment.domain.Comment;
import com.example.community.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateServiceRequest {

    private String content;

    private Long boardId;

    private Long parentId;

    @Builder
    public CommentCreateServiceRequest(String content, Long boardId, Long parentId) {
        this.content = content;
        this.boardId = boardId;
        this.parentId = parentId;
    }

    public Comment toEntity(Board board,  Member member) {
        return Comment.builder()
                .board(board)
                .content(this.content)
                .member(member)
                .build();
    }
}
