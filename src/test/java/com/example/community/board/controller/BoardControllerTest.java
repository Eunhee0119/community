package com.example.community.board.controller;

import com.example.community.board.controller.request.BoardCreateRequest;
import com.example.community.board.controller.request.BoardUpdateRequest;
import com.example.community.board.service.BoardService;
import com.example.community.board.service.response.BoardListResponse;
import com.example.community.board.service.response.BoardResponse;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BoardController.class)
@Import(TestSecurityConfig.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;


    @DisplayName("게시글을 등록한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void registerBoardTest() throws Exception {
        //given
        MockMultipartFile images = new MockMultipartFile("images", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "Hello, World!".getBytes());

        BoardCreateRequest boardCreateRequest = BoardCreateRequest
                .builder()
                .categoryId(1L)
                .title("title")
                .content("content")
                .build();
        String contents = objectMapper.writeValueAsString(boardCreateRequest);

        given(boardService.registerBoard(any())).willReturn(BoardResponse.builder().build());

        //when//then
        mockMvc.perform(multipart("/api/boards")
                        .file(images)
                        .file(new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, contents.getBytes(StandardCharsets.UTF_8)))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }


    @DisplayName("게시글을 등록 시 제목을 입력하지 않으면 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void registerBoardTestWithoutTitle() throws Exception {
        //given
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .categoryId(1L)
                .content("content").build();
        String contents = objectMapper.writeValueAsString(boardCreateRequest);

        given(boardService.registerBoard(any())).willReturn(BoardResponse.builder().build());

        //when//then
        mockMvc.perform(multipart("/api/boards")
                        .file(new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, contents.getBytes(StandardCharsets.UTF_8)))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("제목을 작성해주세요."));
    }


    @DisplayName("게시글을 등록 시 카테고리 아이디를 입력하지 않으면 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void registerBoardTestWithoutCategoryId() throws Exception {
        //given
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title("title")
                .content("content").build();
        String contents = objectMapper.writeValueAsString(boardCreateRequest);

        given(boardService.registerBoard(any())).willReturn(BoardResponse.builder().build());

        //when//then
        mockMvc.perform(multipart("/api/boards")
                        .file(new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, contents.getBytes(StandardCharsets.UTF_8)))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("카테고리를 선택해주세요."));
    }

    @DisplayName("게시글을 등록 시 자연수가 아닌 카테고리 아이디를 입력하면 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void registerBoardTestWithNoExistCategoryId() throws Exception {
        //given
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .categoryId(-1L)
                .title("title")
                .content("content").build();
        String contents = objectMapper.writeValueAsString(boardCreateRequest);

        given(boardService.registerBoard(any())).willReturn(BoardResponse.builder().build());

        //when//then
        mockMvc.perform(multipart("/api/boards")
                        .file(new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, contents.getBytes(StandardCharsets.UTF_8)))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("잘못된 카테고리 정보입니다."));
    }


    @DisplayName("게시글 상세페이지를 조회한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void getBoardDetailsTest() throws Exception {
        //given
        given(boardService.getBoardDetails(any())).willReturn(BoardResponse.builder().build());

        //when//then
        mockMvc.perform(get("/api/boards/" + 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }


    @DisplayName("게시글을 수정한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void updateBoardTest() throws Exception {
        //given
        Long boardId = 1L;
        MockMultipartFile addImageFile = new MockMultipartFile("images", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "Hello, World!".getBytes());

        BoardUpdateRequest updateRequest = BoardUpdateRequest.builder()
                .title("updateTitle")
                .content("updateContent")
                .categoryId(1L)
                .deleteImages(List.of(1L))
                .build();
        String contents = objectMapper.writeValueAsString(updateRequest);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/boards/" + boardId);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        //when//then
        mockMvc.perform(builder
                        .file(addImageFile)
                        .file(new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, contents.getBytes(StandardCharsets.UTF_8)))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()));

        verify(boardService, atLeast(1)).updateBoards(any(), any(), any());
    }

    @DisplayName("게시글을 수정 시 제목을 입력하지 않으면 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void updateBoardWhitEmptyTitleTest() throws Exception {
        //given
        Long boardId = 1L;

        BoardUpdateRequest updateRequest = BoardUpdateRequest.builder()
                .title("")
                .content("updateContent")
                .categoryId(1L)
                .build();
        String contents = objectMapper.writeValueAsString(updateRequest);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/boards/" + boardId);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        //when//then
        mockMvc.perform(builder
                        .file(new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, contents.getBytes(StandardCharsets.UTF_8)))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("제목을 작성해주세요."));
    }

    @DisplayName("게시글을 수정 시 카테고리를 선택하지 않으면 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void updateBoardWhitNoExistCategoryIdTest() throws Exception {
        //given
        Long boardId = 1L;

        BoardUpdateRequest updateRequest = BoardUpdateRequest.builder()
                .title("updateTitle")
                .content("updateContent")
                .build();
        String contents = objectMapper.writeValueAsString(updateRequest);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/boards/" + boardId);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        //when//then
        mockMvc.perform(builder
                        .file(new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, contents.getBytes(StandardCharsets.UTF_8)))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("카테고리를 선택해주세요."));
    }

    @DisplayName("게시글을 수정 시 카테고리를 자연수가 아닌 수로 입력할 경우 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void updateBoardWhitNotPositiveCategoryIdTest() throws Exception {
        //given
        Long boardId = 1L;

        BoardUpdateRequest updateRequest = BoardUpdateRequest.builder()
                .title("updateTitle")
                .content("updateContent")
                .categoryId(-100L)
                .build();
        String contents = objectMapper.writeValueAsString(updateRequest);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/boards/" + boardId);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        //when//then
        mockMvc.perform(builder
                        .file(new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, contents.getBytes(StandardCharsets.UTF_8)))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("잘못된 카테고리 정보입니다."));
    }


    @DisplayName("게시글을 삭제한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void deleteBoardTest() throws Exception {
        //given

        //when//then
        mockMvc.perform(delete("/api/boards/" + 1L))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()));

        verify(boardService, atLeast(1)).deleteBoard(any(), any());
    }


    @DisplayName("게시글 리스트를 조회한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void getBoardListTest() throws Exception {
        //given
        BoardListResponse boardListResponse = new BoardListResponse(List.of(BoardResponse.builder().build()), 1);
        given(boardService.getBoardList(any(), any())).willReturn(boardListResponse);

        //when//then
        mockMvc.perform(get("/api/boards")
                        .param("page", "1")
                        .param("categoryId", String.valueOf(1L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.data.totalPage").value(1))
                .andExpect(jsonPath("$.data.boards").isArray());

        verify(boardService, atLeast(1)).getBoardList(any(), any());
    }

    @DisplayName("게시글 리스트 조회시 카테고리 아이디 값이 없으면 에러가 발생한다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void getBoardListWithoutCategoryIdTest() throws Exception {
        //given
        BoardListResponse boardListResponse = new BoardListResponse(List.of(BoardResponse.builder().build()), 1);
        given(boardService.getBoardList(any(), any())).willReturn(boardListResponse);

        //when//then
        mockMvc.perform(get("/api/boards")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.message").value("카테고리를 선택해주세요."));
    }


    @DisplayName("게시글에 좋아요를 누른다.")
    @Test()
    @WithMockUser(username = "test@test.com", roles = {"USER", "ADMIN"})
    void updateBoardLikeCountTest() throws Exception {
        //given

        //when//then
        mockMvc.perform(post("/api/boards/1/like"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.name()));

        verify(boardService, atLeast(1)).clickBoardLikeCount(any(), any());
    }
}