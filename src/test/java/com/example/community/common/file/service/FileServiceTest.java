package com.example.community.common.file.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FileServiceTest {
    @Autowired
    private FileService fileService;
    String testLocation = new File("src/test/resources/files").getAbsolutePath() + "/";

    @DisplayName("파일을 업로드한다.")
    @Test()
    void fileUploadTest() {
        //given
        MockMultipartFile imageFile = new MockMultipartFile("image1", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "Hello, World!".getBytes());
        String filename = "image1"+ LocalDateTime.now() +".jpg";

        //when
        fileService.upload(imageFile,filename);

        //then
        assertThat(new File(testLocation+ filename).exists()).isTrue();
    }

    @DisplayName("파일을 삭제한다.")
    @Test()
    void fileDeleteTest() throws IOException {
        //given
        MockMultipartFile imageFile = new MockMultipartFile("image1", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "Hello, World!".getBytes());
        String filename = "image1"+ LocalDateTime.now() +".jpg";

        imageFile.transferTo(new File(testLocation + filename));

        assertThat(new File(testLocation+ filename).exists()).isTrue();

        //when
        fileService.delete(filename);

        //then
        assertThat(new File(testLocation+ filename).exists()).isFalse();
    }
}