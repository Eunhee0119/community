package com.example.community.board.service.request;

import com.example.community.board.domain.Image;
import com.example.community.board.factory.ImageFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageCreateServiceRequest {
    private String name;

    public ImageCreateServiceRequest(String name) {
        this.name = name;
    }

    public Image toEntity() {
        return ImageFactory.createImage(name);
    }
}
