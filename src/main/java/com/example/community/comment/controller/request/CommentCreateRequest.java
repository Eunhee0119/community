package com.example.community.comment.controller.request;

import com.example.community.comment.service.request.CommentCreateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequest {

    @NotEmpty(message = "댓글을 작성해주세요.")
    private String content;

    @NotNull(message = "게시글 정보를 확인해주세요.")
    @Positive(message = "게시글 정보를 확인해주세요.")
    private Long boardId;

    @Positive(message = "상위 댓글 정보를 확인해주세요.")
    private Long parentId;


    @Builder
    public CommentCreateRequest(String content, Long boardId, Long parentId) {
        this.content = content;
        this.boardId = boardId;
        this.parentId = parentId;
    }

    public CommentCreateServiceRequest toServiceRequest() {
        return CommentCreateServiceRequest.builder()
                .content(content)
                .boardId(boardId)
                .parentId(parentId)
                .build();
    }
}
