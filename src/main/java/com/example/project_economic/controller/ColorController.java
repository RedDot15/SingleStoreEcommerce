package com.example.project_economic.controller;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.ColorService;
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
@RequestMapping(value = "/color")
public class ColorController {
    ColorService colorService;

    @GetMapping("/list/all")
    public ResponseEntity<ResponseObject> showAll(){
        // Fetch & Return all colors
        return buildResponse(
                HttpStatus.OK,
                "All colors fetch successfully.",
                colorService.getAll()
        );
    }

    @GetMapping("/list/all/by/product/{productId}")
    public ResponseEntity<ResponseObject> showAllColorOfAProduct(@PathVariable Long productId){
        // Return all color by productId
        return buildResponse(
                HttpStatus.OK,
                "All product-colors fetch successfully.",
                colorService.getAllByProductId(productId)
        );
    }

    @GetMapping("/list/active/by/product/{productId}")
    public ResponseEntity<ResponseObject> showActiveColorOfAProduct(@PathVariable Long productId){
        // Return active color by productId
        return buildResponse(
                HttpStatus.OK,
                "Active colors fetch successfully.",
                colorService.getActiveByProductId(productId)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> add(@Validated({Create.class,Default.class}) @RequestBody ColorRequest colorRequest){
        // Add & Return color
        return buildResponse(
                HttpStatus.OK,
                "Created new color successfully.",
                colorService.add(colorRequest)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> update(@Validated({Update.class,Default.class}) @RequestBody ColorRequest colorRequest){
        // Update & Return color
        return buildResponse(
                HttpStatus.OK,
                "Updated color successfully.",
                colorService.update(colorRequest)
        );
    }

    @DeleteMapping("/{colorId}/delete")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long colorId){
        // Delete & Return id
        return buildResponse(
                HttpStatus.OK,
                "Deleted color successfully.",
                colorService.delete(colorId)
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
