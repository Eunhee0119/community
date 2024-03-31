package com.example.community.board.domain.dto;

import com.example.community.board.domain.Image;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDto {

    private Long id;

    private Long categoryId;

    private String categoryName;

    private String title;

    @Lob
    private String content;

    private List<Image> images = new ArrayList<>();

    private String writer;

    private int hitCnt = 0;

    private int likeCount = 0;
}
