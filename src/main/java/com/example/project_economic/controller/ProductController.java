package com.example.project_economic.controller;

import com.example.project_economic.dto.request.*;
import com.example.project_economic.dto.request.filter.ProductFilterRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.*;
import com.example.project_economic.validation_group.Create;
import com.example.project_economic.validation_group.Update;
import jakarta.validation.groups.Default;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/product")
public class ProductController {
    ProductService productService;
    ProductDetailService productDetailService;
    ProductImageService productImageService;
    ColorService colorService;
    SizeService sizeService;

    @GetMapping("/list/all")
    public ResponseEntity<ResponseObject> showAll() {
        // Handle empty result & Return products
        return buildResponse(
                HttpStatus.OK,
                "All products fetched successfully.",
                productService.getAll()
        );
    }

    @GetMapping("/list/active/filter-by")
    public ResponseEntity<ResponseObject> showActiveByFilter(
            @RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize,
            @ModelAttribute ProductFilterRequest productFilterRequest
    ){
        // Return active products
        return buildResponse(
                HttpStatus.OK, 
                "Active products fetch successfully.",
                productService.getActiveByFilter(
                        pageNumber, pageSize,
                        productFilterRequest
                )
        );
    }

    @GetMapping("/list/active/by/category/{categoryId}")
    public ResponseEntity<ResponseObject> showActiveProductOfACategory(@PathVariable Long categoryId){
        // Fetch & Return the active products
        return buildResponse(
                HttpStatus.OK,
                "Active products fetched successfully.",
                productService.getActiveByCategoryId(categoryId)
        );
    }

    @GetMapping("/{productId}/get/active")
    public ResponseEntity<ResponseObject> getActiveById(@PathVariable Long productId){
        // Return active product by productId
        return buildResponse(
                HttpStatus.OK,
                "Product fetch successfully.",
                productService.getActiveById(productId)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> add(@Validated({Create.class,Default.class}) @RequestBody ProductRequest productRequest){
        // Add & Return product
        return buildResponse(
                HttpStatus.CREATED,
                "Created new product successfully.",
                productService.add(productRequest)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> update(@Validated({Update.class,Default.class}) @RequestBody ProductRequest productRequest){
        // Update & Return product
        return buildResponse(
                HttpStatus.OK,
                "Updated product: " + productRequest.getId() + " successfully.",
                productService.update(productRequest)
        );
    }

    @DeleteMapping("/{productId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long productId) {
        // Delete product & Return
        return buildResponse(
                HttpStatus.OK,
                "Deleted product successfully.",
                productService.delete(productId)
        );
    }

    @PutMapping("/{productId}/activate")
    public ResponseEntity<ResponseObject> activate(@PathVariable Long productId) {
        // Activate & Return
        return buildResponse(
                HttpStatus.OK,
                "Activated product successfully.",
                productService.activate(productId)
        );
    }

    @GetMapping("/{productId}/deactivate/check")
    public ResponseEntity<ResponseObject> deactivateCheck(@PathVariable Long productId){
        // Return check message
        return buildResponse(
                HttpStatus.OK,
                "Deactivate check message fetch successfully.",
                productService.getDeactivateCheckMessage(productId)
        );
    }

    @PutMapping("/{productId}/deactivate")
    public ResponseEntity<ResponseObject> deactivate(@PathVariable Long productId) {
        // Deactivate & Return
        return buildResponse(
                HttpStatus.OK,
                "Deactivated product successfully.",
                productService.deactivate(productId)
        );
    }

    @PutMapping("/{productId}/like")
    public ResponseEntity<ResponseObject> like(@PathVariable Long productId) {
        return buildResponse(
                HttpStatus.OK,
                "Updated number of like for product: " + productId + " successfully.",
                productService.like(productId)
        );
    }

    private ResponseEntity<ResponseObject> buildResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(
                new ResponseObject(
                        status.is2xxSuccessful() ? "ok" : "failed",
                        message,
                        data
                )
        );
    }
}
