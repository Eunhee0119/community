package com.example.community.board.service.response;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.dto.BoardDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedDateTime;


    @Builder
    public BoardResponse(Long id, Long categoryId, String title, String content, String writer, List<ImageResponse> images, int hitCount, int likeCount, LocalDateTime createDateTime, LocalDateTime modifiedDateTime) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.images = images;
        this.hitCount = hitCount;
        this.likeCount = likeCount;
        this.createDateTime = createDateTime;
        this.modifiedDateTime = modifiedDateTime;
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
                .createDateTime(board.getCreateDateTime())
                .modifiedDateTime(board.getModifiedDateTime())
                .build();
    }

    public static BoardResponse of(BoardDto board) {
        return BoardResponse.builder()
                .id(board.getId())
                .categoryId(board.getCategoryId())
                .title(board.getTitle())
                .content(board.getContent())
                .images(ImageResponse.of(board.getImages()))
                .writer(board.getWriter())
                .hitCount(board.getHitCnt())
                .likeCount(board.getLikeCount())
                .createDateTime(board.getCreateDateTime())
                .modifiedDateTime(board.getModifiedDateTime())
                .build();
    }
}
