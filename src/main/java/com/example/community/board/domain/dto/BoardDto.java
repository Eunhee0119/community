package com.example.community.board.domain.dto;

import com.example.community.board.domain.Image;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDto {

    private Long id;

    private Long categoryId;

    private String title;

    @Lob
    private String content;

    private List<Image> images = new ArrayList<>();

    private String writer;

    private int hitCnt = 0;

    private int likeCount = 0;

    private LocalDateTime createDateTime;

    private LocalDateTime modifiedDateTime;

    @Builder
    public BoardDto(Long id, Long categoryId, String title, String content, List<Image> images, String writer, int hitCnt, int likeCount, LocalDateTime createDateTime, LocalDateTime modifiedDateTime) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.images = images;
        this.writer = writer;
        this.hitCnt = hitCnt;
        this.likeCount = likeCount;
        this.createDateTime = createDateTime;
        this.modifiedDateTime = modifiedDateTime;
    }
}
