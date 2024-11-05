package com.example.project_economic.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface StorageService {
    String uploadFile(MultipartFile multipartFile);

    byte[] downloadFile(String fileName);

    String deleteFile(String fileName);
}
