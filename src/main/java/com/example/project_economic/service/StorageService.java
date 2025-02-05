package com.example.project_economic.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	// Upload/Download/Delete
	String uploadFile(MultipartFile multipartFile);

	byte[] downloadFile(String fileName);

	String deleteFile(String fileName);
}
