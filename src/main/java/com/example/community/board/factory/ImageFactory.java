package com.example.community.board.factory;


import com.example.community.board.domain.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ImageFactory {
    public static Image createImage() {
        return new Image("temp.jpg");
    }

    public static List<Image> createImages(List<MultipartFile> imageFiles) {
        return Objects.isNull(imageFiles) ? List.of() : imageFiles.stream()
                .map(it -> ImageFactory.createImage(it.getOriginalFilename()))
                .collect(Collectors.toList());
    }

    public static Image createImage(String originName) {
        return new Image(originName);
    }
}
