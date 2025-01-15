package com.example.project_economic.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService{
    @Value("${application.cloudfront.name}")
    private String cloudFrontName;

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    @Qualifier("s3ClientRead")
    private AmazonS3 s3ClientRead;

    @Autowired
    @Qualifier("s3ClientWrite")
    private AmazonS3 s3ClientWrite;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPLOAD_IMAGE_FILE')")
    public String uploadFile(MultipartFile file) {
        try{
            if (file.isEmpty()){
                throw new AppException(ErrorCode.EMPTY_IMAGE_FILE);
            }
            //check if file is image?
            if (!isImageFile(file)){
                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
            }
            //file must be <= 5MB
            float fileSizeInMegabytes = (float) file.getSize() / 1_000_000;
            if (fileSizeInMegabytes > 5.0f){
                throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
            }
            File fileObj = convertMultiPartFileToFile(file);
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            s3ClientWrite.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            fileObj.delete();
            return fileName;
        }
        catch (Exception exception){
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }


    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3ClientRead.getObject(cloudFrontName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_IMAGE_FILE')")
    public String deleteFile(String fileName) {
        s3ClientWrite.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }

    private boolean isImageFile(MultipartFile file){
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        return Arrays.asList(new String[] {"png","jpg","jpeg","bmp","webp","jfif"})
                .contains(fileExtension.trim().toLowerCase());
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
