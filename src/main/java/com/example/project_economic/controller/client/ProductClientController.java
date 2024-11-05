package com.example.project_economic.controller.client;

import com.example.project_economic.dto.response.ProductDetailResponse;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.dto.response.ResponseObject;
import com.example.project_economic.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/client/product")
public class ProductClientController {
    ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getFirstProductById(@PathVariable Long productId){
        ProductResponse productResponse = productService.getFirstById(productId);
        return (productResponse != null) ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Query product successfully", productResponse)
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Cannot find product with id = " + productId, "")
                );
    }
}
