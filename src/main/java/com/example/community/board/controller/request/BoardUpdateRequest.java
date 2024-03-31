package com.example.community.board.controller.request;

import com.example.community.board.service.request.BoardUpdateServiceRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardUpdateRequest {

    private Long categoryId;

    private String title;

    private String content;

    private List<MultipartFile> addedImages = new ArrayList<>();

    private List<Long> deleteImages = new ArrayList<>();

    @Builder
    public BoardUpdateRequest(Long categoryId, String title, String content,  List<MultipartFile> addedImages, List<Long> deleteImages) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.addedImages.addAll(addedImages);
        this.deleteImages.addAll(deleteImages);
    }

    public BoardUpdateServiceRequest toServiceRequest() {
        return BoardUpdateServiceRequest.builder()
                .categoryId(this.categoryId)
                .title(this.title)
                .content(this.content)
                .addedImages(this.addedImages)
                .deletedImages(this.deleteImages)
                .build();
    }
}
