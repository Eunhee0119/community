package com.example.community.board.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BoardSearchDto {

    @NotNull(message = "카테고리를 선택해주세요.")
    @Positive(message = "잘못된 카테고리 정보입니다.")
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
