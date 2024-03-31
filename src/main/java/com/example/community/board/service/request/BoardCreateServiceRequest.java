package com.example.community.board.service.request;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.Image;
import com.example.community.board.factory.ImageFactory;
import com.example.community.category.domain.Category;
import com.example.community.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCreateServiceRequest {

    private Long categoryId;

    private String title;

    private String content;

    private String writer;

    private List<MultipartFile> images;

    @Builder
    public BoardCreateServiceRequest(Long categoryId, String title, String content, String writer, List<MultipartFile> images) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.images = images;
    }

    public Board toEntity(Member member, Category category,List<Image> images) {
        Board board = Board.builder()
                .category(category)
                .title(title)
                .content(content)
                .images(images)
                .member(member)
                .build();

        return board;
    }
}
