package com.example.project_economic.controller;

import com.example.project_economic.dto.response.ProductImageResponse;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/product-image")
public class ProductImageController {
    ProductService productService;

//    @GetMapping("/get/{id}")
//    public ResponseEntity<byte[]>findProductById(@PathVariable Long id){
//        ProductImageResponse productImageResponse = productService.findProductImageResponseById(id);
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(productImageResponse.getImageType()))
//                .body(productImageResponse.getData());
//    }
}