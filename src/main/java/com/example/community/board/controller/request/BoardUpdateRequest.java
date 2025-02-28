package com.example.community.board.controller.request;

import com.example.community.board.service.request.BoardUpdateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardUpdateRequest {

    @NotNull(message = "카테고리를 선택해주세요.")
    @Positive(message = "잘못된 카테고리 정보입니다.")
    private Long categoryId;

    @NotEmpty(message = "제목을 작성해주세요.")
    private String title;

    private String content;

    private List<Long> deleteImages = new ArrayList<>();

    @Builder
    public BoardUpdateRequest(Long categoryId, String title, String content,  List<Long> deleteImages) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        if(!Objects.isNull(deleteImages)) this.deleteImages.addAll(deleteImages);
    }

    public BoardUpdateServiceRequest toServiceRequest(List<MultipartFile> addedImages) {
        return BoardUpdateServiceRequest.builder()
                .categoryId(this.categoryId)
                .title(this.title)
                .content(this.content)
                .addedImages(addedImages)
                .deletedImages(this.deleteImages)
                .build();
    }
}
