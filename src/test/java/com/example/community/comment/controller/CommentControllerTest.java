package com.example.community.comment.controller;

import com.example.community.comment.controller.request.CommentCreateRequest;
import com.example.community.comment.controller.request.CommentUpdateRequest;
import com.example.community.comment.service.CommentService;
import com.example.community.comment.service.response.CommentResponse;
import com.example.community.config.jwt.JwtTokenProvider;
import com.example.util.jwt.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
@Import(TestSecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("댓글을 등록한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void registerCommentTest() throws Exception {
        //given
        given(commentService.registerComment(any(), any())).willReturn(CommentResponse.builder().build());

        CommentCreateRequest commentRequest = CommentCreateRequest.builder()
                .boardId(1L)
                .content("content")
                .parentId(1L)
                .build();

        //when//then
        mockMvc.perform(post("/api/comments")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }


    @DisplayName("댓글 등록 시 내용을 작성하지 않는 경우 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void registerCommentWhenEmptyContentsTest() throws Exception {
        //given
        given(commentService.registerComment(any(), any())).willReturn(CommentResponse.builder().build());

        CommentCreateRequest commentRequest = CommentCreateRequest.builder()
                .boardId(1L)
                .content("")
                .parentId(1L)
                .build();

        //when//then
        mockMvc.perform(post("/api/comments")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("댓글을 작성해주세요."));
    }

    @DisplayName("댓글 등록 시 게시글 아이디는 null일 경우 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void registerCommentWhenNullBoardIdTest() throws Exception {
        //given
        given(commentService.registerComment(any(), any())).willReturn(CommentResponse.builder().build());

        CommentCreateRequest commentRequest = CommentCreateRequest.builder()
                .content("content")
                .parentId(1L)
                .build();

        //when//then
        mockMvc.perform(post("/api/comments")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("게시글 정보를 확인해주세요."));
    }


    @DisplayName("댓글 등록 시 게시글 아이디가 음수일 경우 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void registerCommentWhenNotPositiveBoardIdTest() throws Exception {
        //given
        given(commentService.registerComment(any(), any())).willReturn(CommentResponse.builder().build());

        CommentCreateRequest commentRequest = CommentCreateRequest.builder()
                .boardId(-1L)
                .content("content")
                .parentId(1L)
                .build();

        //when//then
        mockMvc.perform(post("/api/comments")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("게시글 정보를 확인해주세요."));
    }


    @DisplayName("댓글 등록 시 상위 댓글 아이디가 음수일 경우 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void registerCommentWhenNotPositiveParentIdTest() throws Exception {
        //given
        given(commentService.registerComment(any(), any())).willReturn(CommentResponse.builder().build());

        CommentCreateRequest commentRequest = CommentCreateRequest.builder()
                .boardId(1L)
                .content("content")
                .parentId(-11L)
                .build();

        //when//then
        mockMvc.perform(post("/api/comments")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("상위 댓글 정보를 확인해주세요."));
    }

    @DisplayName("댓글을 수정한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void updateCommentTest() throws Exception {
        //given
        CommentUpdateRequest commentRequest = CommentUpdateRequest.builder()
                .commentId(1L)
                .content("content")
                .build();

        //when//then
        mockMvc.perform(patch("/api/comments/1")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()));

        verify(commentService, atLeast(1)).updateComment(any(), any());
    }


    @DisplayName("댓글 수정 시 댓글 아이디가 null 일 경우 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void updateCommentWhenNullCommentIdTest() throws Exception {
        //given
        CommentUpdateRequest commentRequest = CommentUpdateRequest.builder()
                .content("content")
                .build();

        //when//then
        mockMvc.perform(patch("/api/comments/1")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("댓글 정보가 잘못되었습니다."));
    }


    @DisplayName("댓글 수정 시 댓글 아이디가 음수일 경우 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void updateCommentWhenNotPositiveCommentIdTest() throws Exception {
        //given
        CommentUpdateRequest commentRequest = CommentUpdateRequest.builder()
                .commentId(-1L)
                .content("content")
                .build();

        //when//then
        mockMvc.perform(patch("/api/comments/-1")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("댓글 정보가 잘못되었습니다."));
    }


    @DisplayName("댓글 수정 시 내용이 없을 경우 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void updateCommentWhenEmptyContentsTest() throws Exception {
        //given
        CommentUpdateRequest commentRequest = CommentUpdateRequest.builder()
                .commentId(1L)
                .content("")
                .build();

        //when//then
        mockMvc.perform(patch("/api/comments/1")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("댓글을 작성해주세요."));
    }


    @DisplayName("댓글을 삭제한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void deleteCommentTest() throws Exception {
        //given
        //when//then
        mockMvc.perform(delete("/api/comments/1"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()));

        verify(commentService, atLeast(1)).deletedComment(any(), any());
    }


    @DisplayName("댓글에 좋아요를 누른다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void clickCommentLikeTest() throws Exception {
        //given
        //when//then
        mockMvc.perform(post("/api/comments/1/like"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()));

        verify(commentService, atLeast(1)).clickCommentLike(any(), any());
    }


    @DisplayName("게시글 아이디로 댓글리스트를 조회한다.")
    @Test()
    void getCommentListByBoardIdTest() throws Exception {
        //given
        given(commentService.getCommentListByBoardId(any())).willReturn(List.of());
        //when//then
        mockMvc.perform(get("/api/comments")
                        .param("boardId","1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data").isArray());

        verify(commentService, atLeast(1)).getCommentListByBoardId(any());
    }
}