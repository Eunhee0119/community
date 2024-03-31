package com.example.community.board.service.request;

import com.example.community.board.domain.Board;
import com.example.community.board.domain.Image;
import com.example.community.board.factory.ImageFactory;
import com.example.community.category.domain.Category;
import com.example.community.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class BoardUpdateServiceRequest {

    private Long categoryId;

    private String title;

    private String content;

    private String writer;

    private List<MultipartFile> addedImages = new ArrayList<>();

    private List<Long> deletedImages = new ArrayList<>();


    @Builder
    public BoardUpdateServiceRequest(Long categoryId, String title, String content, String writer, List<MultipartFile> addedImages, List<Long> deletedImages) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.addedImages = addedImages;
        this.deletedImages = deletedImages;
    }
}
