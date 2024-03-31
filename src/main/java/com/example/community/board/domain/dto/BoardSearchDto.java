package com.example.community.board.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardSearchDto {


    private Long categoryId;

    private String title;

    private String writer;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;


    @Builder
    public BoardSearchDto(Long categoryId, String title, String writer, LocalDateTime startDate, LocalDateTime endDate) {
        this.categoryId = categoryId;
        this.title = title;
        this.writer = writer;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
