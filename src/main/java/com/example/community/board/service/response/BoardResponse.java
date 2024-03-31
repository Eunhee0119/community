package com.example.community.board.service.response;

import com.example.community.board.domain.Board;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardResponse {
    private Long id;


    private Long categoryId;

    private String title;

    private String content;

    private String writer;

    private List<ImageResponse> images;

    private int hitCount;

    private int likeCount;


    @Builder
    public BoardResponse(Long id, Long categoryId, String title, String content, String writer, List<ImageResponse> images, int hitCount, int likeCount) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.images = images;
        this.hitCount = hitCount;
        this.likeCount = likeCount;
    }

    public static BoardResponse of(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .categoryId(board.getCategory().getId())
                .title(board.getTitle())
                .content(board.getContent())
                .images(ImageResponse.of(board.getImages()))
                .writer(board.getMember().getEmail())
                .hitCount(board.getHitCnt())
                .likeCount(board.getLikeCount())
                .build();
    }
}
