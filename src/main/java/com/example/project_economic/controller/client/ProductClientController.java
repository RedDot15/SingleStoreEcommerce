package com.example.project_economic.controller.client;

import com.example.project_economic.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/client/product")
public class ProductClientController {
    ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetail(@PathVariable Long productId){
        return new ResponseEntity<>(productService.getById(productId), HttpStatus.OK);
    }
}
