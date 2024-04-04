package com.example.community.board.controller.request;

import com.example.community.board.service.request.BoardCreateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCreateRequest {

    @NotNull(message = "카테고리를 선택해주세요.")
    @Positive(message = "잘못된 카테고리 정보입니다.")
    private Long categoryId;

    @NotEmpty(message = "제목을 작성해주세요.")
    private String title;

    private String content;


    @Builder
    public BoardCreateRequest(Long categoryId, String title, String content) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
    }

    public BoardCreateServiceRequest toServiceRequest(String writer, List<MultipartFile> imageFiles) {
        return BoardCreateServiceRequest.builder()
                .categoryId(this.categoryId)
                .title(this.title)
                .content(this.content)
                .writer(writer)
                .images(imageFiles)
                .build();
    }
}
