package com.example.project_economic.controller;

import com.example.project_economic.dto.request.ProductDetailRequest;
import com.example.project_economic.dto.response.ProductDetailResponse;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.ProductDetailService;
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
@RequestMapping(path = "/product-detail")
public class ProductDetailController {
    ProductDetailService productDetailService;

    @GetMapping("/list/all/by/product/{productId}")
    public ResponseEntity<ResponseObject> showAllProductDetailOfAProduct(@PathVariable Long productId){
        // Return all products-detail by productId
        return buildResponse(
                HttpStatus.OK,
                "All product-detail fetch successfully.",
                productDetailService.getAllByProductId(productId)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> add(@Validated({Create.class, Default.class}) @RequestBody ProductDetailRequest productDetailRequest){
        // Add product-detail
        ProductDetailResponse productDetailResponse = productDetailService.add(productDetailRequest);
        // Get http status
        HttpStatus httpStatus = productDetailResponse.getIsNew() ? HttpStatus.CREATED : HttpStatus.OK;
        // Get message
        String message = productDetailResponse.getIsNew()
                ? "Created new product detail successfully."
                : "Product detail ID: " + productDetailResponse.getId() + " added " +
                productDetailRequest.getStock() + " more stock successfully.";
        // Return
        return buildResponse(httpStatus, message, productDetailResponse);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> update(@Validated({Update.class, Default.class}) @RequestBody ProductDetailRequest productDetailRequest){
        // Update & Return product-detail
        return buildResponse(
                HttpStatus.OK,
                "Updated product detail id: " + productDetailRequest.getId() + " successfully",
                productDetailService.update(productDetailRequest)
        );
    }

    @DeleteMapping("/{productDetailId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long productDetailId){
        // Delete product-detail & Return
        return buildResponse(
                HttpStatus.OK,
                "Deleted product-detail successfully.",
                productDetailService.delete(productDetailId)
        );
    }

    @PutMapping("/{productDetailId}/activate")
    public ResponseEntity<ResponseObject> activate(@PathVariable Long productDetailId) {
        // Activate & Return
        return buildResponse(
                HttpStatus.OK,
                "Activated product-detail successfully.",
                productDetailService.activate(productDetailId)
        );
    }

    @GetMapping("/{productDetailId}/deactivate/check")
    public ResponseEntity<ResponseObject> deactivateCheck(@PathVariable Long productDetailId){
        // Return check message
        return buildResponse(
                HttpStatus.OK,
                "Deactivate check message fetch successfully.",
                productDetailService.getDeactivateCheckMessage(productDetailId)
        );
    }

    @PutMapping("/{productDetailId}/deactivate")
    public ResponseEntity<ResponseObject> deactivate(@PathVariable Long productDetailId) {
        // Deactivate & Return
        return buildResponse(
                HttpStatus.OK,
                "Deactivated product-detail successfully.",
                productDetailService.deactivate(productDetailId)
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
