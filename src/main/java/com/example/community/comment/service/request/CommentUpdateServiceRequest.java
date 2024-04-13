package com.example.community.comment.service.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUpdateServiceRequest {

    private Long commentId;

    private String content;

    @Builder
    public CommentUpdateServiceRequest(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }
}
