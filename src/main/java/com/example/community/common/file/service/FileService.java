package com.example.community.common.file.service;

import com.example.community.common.file.exception.FileEmptyException;
import com.example.community.common.file.exception.FileUploadFailureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class FileService {

    @Value("${file.location}")
    private String location;

    @PostConstruct
    void postConstruct() {
        File dir = new File(location);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    public void upload(MultipartFile file, String name) {
        if (file.isEmpty()) {
            throw new FileEmptyException("존재하지 않는 파일입니다.");
        }
        try {
            file.transferTo(new File(location +"/"+ name));
        } catch (IOException e) {
            throw new FileUploadFailureException("파일 업로드에 실패했습니다.");
        }
    }

    public void delete(String name) {
        new File(location +"/"+ name).delete();
    }
}
