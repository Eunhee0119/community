package com.example.community.comment.controller;

import com.example.community.ApiResponse;
import com.example.community.comment.controller.request.CommentCreateRequest;
import com.example.community.comment.controller.request.CommentUpdateRequest;
import com.example.community.comment.service.CommentService;
import com.example.community.comment.service.response.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/comments")
    public ApiResponse<CommentResponse> registerComment(@RequestBody @Valid CommentCreateRequest commentCreateRequest
            , Principal principal) {
        CommentResponse commentResponse = commentService.registerComment(commentCreateRequest.toServiceRequest(), principal.getName());
        return ApiResponse.ok(commentResponse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/api/comments/{id}")
    public ApiResponse<CommentResponse> updateComment(@RequestBody @Valid CommentUpdateRequest commentUpdateRequest
            , Principal principal) {
        commentService.updateComment(commentUpdateRequest.toServiceRequest(), principal.getName());
        return ApiResponse.noContent();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/comments/{id}")
    public ApiResponse<CommentResponse> deleteComment(@PathVariable(name = "id") Long commentId
            , Principal principal) {
        commentService.deletedComment(commentId, principal.getName());
        return ApiResponse.noContent();
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/api/comments/{id}/like")
    public ApiResponse<CommentResponse> clickCommentLike(@PathVariable(name = "id") Long commentId
            , Principal principal) {
        commentService.clickCommentLike(commentId, principal.getName());
        return ApiResponse.noContent();
    }

    @GetMapping("/api/comments")
    public ApiResponse<List<CommentResponse>> getComments(@RequestParam(name = "boardId") Long boardId) {
        return ApiResponse.ok(commentService.getCommentListByBoardId(boardId));
    }

}
