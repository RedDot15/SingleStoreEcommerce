package com.example.project_economic.controller.client;

import com.example.project_economic.service.SizeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(value = "/client/size")
public class SizeClientController {
    SizeService sizeService;

//    @GetMapping(value = "/{productId}")
//    public ResponseEntity<?> getAllSizesByProductId(@PathVariable Long productId){
//        return new ResponseEntity<>(sizeService.getAllSizeByProductId(productId),HttpStatus.OK);
//    }

}
