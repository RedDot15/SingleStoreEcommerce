package com.example.project_economic.controller.client;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.service.ColorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(value = "/client/color")
public class ColorClientController {
    ColorService colorService;

    @GetMapping(value = "/{productId}")
    public ResponseEntity<?> getAllColorOfAProduct(@PathVariable Long productId){
        return new ResponseEntity<>(colorService.getAllColorOfAProduct(productId), HttpStatus.OK);
    }
}
