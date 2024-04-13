package com.example.community.comment.service.response;

import com.example.community.board.service.response.BoardResponse;
import com.example.community.comment.domain.Comment;
import com.example.community.member.service.response.MemberResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;

@Getter
public class CommentResponse {

    private Long id;

    private String content;

    private int deleted;

    private MemberResponse member;

    private BoardResponse board;

    private List<CommentResponse> children = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedDateTime;


    @Builder
    public CommentResponse(Long id, String content, int deleted, MemberResponse member, BoardResponse board, List<CommentResponse> children, LocalDateTime createDateTime, LocalDateTime modifiedDateTime) {
        this.id = id;
        this.content = content;
        this.deleted = deleted;
        this.member = member;
        this.board = board;
        this.createDateTime = createDateTime;
        this.modifiedDateTime = modifiedDateTime;

        if(!isNull(children)) children.stream().forEach(it->addChildren(it));
    }


    public static CommentResponse of(Comment comment) {
        CommentResponse commentResponse = CommentResponse.builder()
                .id(comment.getId())
                .board(BoardResponse.of(comment.getBoard()))
                .content(comment.getContent())
                .member(MemberResponse.of(comment.getMember()))
                .deleted(comment.getDeleted())
                .createDateTime(comment.getCreateDateTime())
                .modifiedDateTime(comment.getModifiedDateTime())
                .build();

        comment.getChildren().stream().forEach(c->commentResponse.addChildren(CommentResponse.of(c)));

        return commentResponse;
    }

    public static List<CommentResponse> of(List<Comment> comments) {
        List<CommentResponse> commentResponseList = new LinkedList<>();
        Map<Long, CommentResponse> commentMap = new HashMap<>();
        comments.stream().forEach(comment -> {
            CommentResponse commentResponse = CommentResponse.of(comment);
            Comment parent = comment.getParent();
            if (isNull(parent)) commentResponseList.add(commentResponse);
            else if (commentMap.containsKey(parent.getId())) {
                CommentResponse parentResponse = commentMap.get(comment.getParent().getId());
                parentResponse.addChildren(commentResponse);
            }
            commentMap.put(commentResponse.getId(), commentResponse);
        });
        return commentResponseList;
    }

    private void addChildren(CommentResponse comment) {
        this.children.add(comment);
    }
}
