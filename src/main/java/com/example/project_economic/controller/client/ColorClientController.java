package com.example.project_economic.controller.client;

import com.example.project_economic.dto.response.ColorResponse;
import com.example.project_economic.dto.response.ResponseObject;
import com.example.project_economic.service.ColorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(value = "/client/color")
public class ColorClientController {
    ColorService colorService;

//    @GetMapping(value = "/")
//    public ResponseEntity<?> getAllColorsByProductId(@RequestParam Long productId){
//        return new ResponseEntity<>(colorService.getAllColorOfAProduct(productId), HttpStatus.OK);
//    }
}
