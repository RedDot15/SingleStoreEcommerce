package com.example.project_economic.mapper;

import com.example.project_economic.dto.response.ProductImageResponse;
import com.example.project_economic.entity.ProductImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    //ProductImageMapper
    @Mapping(target = "productResponse", source = "productEntity")
    @Mapping(target = "colorResponse", source = "colorEntity")
    ProductImageResponse toProductImageResponse(ProductImageEntity productImageEntity);

//    @Mapping(source = "name", target = "name")
//    @Mapping(source = "bytes", target = "data")
//    @Mapping(source = "contentType", target = "imageType")
//    ProductImageEntity toProductImageEntity(MultipartFile multipartFile) throws IOException;
}
