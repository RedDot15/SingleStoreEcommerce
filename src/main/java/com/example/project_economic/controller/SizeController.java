package com.example.project_economic.controller;

import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.SizeService;
import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
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
@RequestMapping(value = "/size")
public class SizeController {
    SizeService sizeService;

    @GetMapping("/list/all")
    public ResponseEntity<ResponseObject> showAll(){
        // Fetch & Return all sizes
        return buildResponse(
                HttpStatus.OK,
                "All sizes fetch successfully.",
                sizeService.getAll()
        );
    }

    @GetMapping("/list/active/by/product/{productId}")
    public ResponseEntity<ResponseObject> showActiveSizeOfAProduct(@PathVariable Long productId){
        // Return active size by productId
        return buildResponse(
                HttpStatus.OK,
                "Active sizes fetch successfully.",
                sizeService.getActiveByProductId(productId)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> add(@Validated({Create.class, Default.class}) @RequestBody SizeRequest sizeRequest){
        // Create & Return size
        return buildResponse(
                HttpStatus.OK,
                "Created new size successfully.",
                sizeService.add(sizeRequest)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> update(@Validated({Update.class, Default.class}) @RequestBody SizeRequest sizeRequest){
        // Update & Return size
        return buildResponse(
                HttpStatus.OK,
                "Updated size successfully",
                sizeService.update(sizeRequest)
        );
    }

    @DeleteMapping("/{sizeId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long sizeId){
        // Delete & Return id
        return buildResponse(
                HttpStatus.OK,
                "Deleted size successfully.",
                sizeService.delete(sizeId)
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
