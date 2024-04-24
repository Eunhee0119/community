package com.example.community.board.controller;

import com.example.community.ApiResponse;
import com.example.community.board.controller.request.BoardCreateRequest;
import com.example.community.board.controller.request.BoardUpdateRequest;
import com.example.community.board.domain.dto.BoardSearchDto;
import com.example.community.board.service.BoardService;
import com.example.community.board.service.request.BoardCreateServiceRequest;
import com.example.community.board.service.response.BoardListResponse;
import com.example.community.board.service.response.BoardResponse;
import com.example.community.category.service.response.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/api/boards")
    public ApiResponse<BoardResponse> registerBoard(@RequestPart(name = "boardCreateRequest" ) @Valid BoardCreateRequest boardCreateRequest,
                                                    @RequestPart(name = "images" ,required = false) List<MultipartFile> images,
                                                    Principal principal){
        BoardCreateServiceRequest createServiceRequest = boardCreateRequest.toServiceRequest(principal.getName(),images);
        return ApiResponse.ok(boardService.registerBoard(createServiceRequest));
    }


    @GetMapping("/api/boards/{id}")
    public ApiResponse<BoardResponse> getBoardDetails(@PathVariable(value = "id") Long boardId){
        return ApiResponse.ok(boardService.getBoardDetails(boardId));
    }


    @PutMapping("/api/boards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<CategoryResponse> updateBoard(@PathVariable(value = "id") Long boardId,
                                                     @Valid @RequestPart(name = "boardUpdateRequest") BoardUpdateRequest boardUpdateRequest,
                                                     @RequestPart(name = "images" ,required = false) List<MultipartFile> addedImages,
                                                     Principal principal) {
        boardService.updateBoards(boardId, boardUpdateRequest.toServiceRequest(addedImages),principal.getName());
        return ApiResponse.noContent();
    }

    @DeleteMapping("/api/boards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<CategoryResponse> updateBoard(@PathVariable(value = "id") Long id , Principal principal) {
        boardService.deleteBoard(id,principal.getName());
        return ApiResponse.noContent();
    }


    @GetMapping("/api/boards")
    public ApiResponse<BoardListResponse> getBoardList(
            @PageableDefault(size=10, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
            @Valid @ModelAttribute("boardSearchDto") BoardSearchDto boardSearchDto){
        BoardListResponse boardList = boardService.getBoardList(pageable, boardSearchDto);
        return ApiResponse.ok(boardList);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/api/boards/{id}/like")
    public ApiResponse<BoardListResponse> updateLike(@PathVariable(value = "id") Long boardId,
                                                     Principal principal){
        boardService.clickBoardLikeCount(boardId, principal.getName());
        return ApiResponse.noContent();
    }
}
