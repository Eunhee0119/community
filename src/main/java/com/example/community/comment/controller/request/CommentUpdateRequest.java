package com.example.community.comment.controller.request;

import com.example.community.comment.service.request.CommentUpdateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUpdateRequest {

    @NotNull(message = "댓글 정보가 잘못되었습니다.")
    @Positive(message = "댓글 정보가 잘못되었습니다.")
    private Long commentId;

    @NotEmpty(message = "댓글을 작성해주세요.")
    private String content;

    @Builder
    public CommentUpdateRequest(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }

    public CommentUpdateServiceRequest toServiceRequest() {
        return CommentUpdateServiceRequest.builder()
                .commentId(commentId)
                .content(content)
                .build();
    }
}
