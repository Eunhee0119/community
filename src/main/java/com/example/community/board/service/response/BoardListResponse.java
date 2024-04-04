package com.example.community.board.service.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardListResponse {

    private List<BoardResponse> boards;
    private int totalPage;

    public BoardListResponse(List<BoardResponse> boards, int totalPage) {
        this.boards = boards;
        this.totalPage = totalPage;
    }
}
