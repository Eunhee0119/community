package com.example.community.board.service.response;

import com.example.community.board.domain.Image;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class ImageResponse {

    private Long id;

    private String saveName;

    private String originName;

    @Builder
    public ImageResponse(Long id, String saveName, String originName) {
        this.id = id;
        this.saveName = saveName;
        this.originName = originName;
    }

    public static ImageResponse of(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .saveName(image.getSaveName())
                .originName(image.getOriginName())
                .build();
    }

    public static List<ImageResponse> of(List<Image> images) {
        if(Objects.isNull(images)) return List.of();
        return images.stream().map(ImageResponse::of).collect(Collectors.toList());
    }
}
