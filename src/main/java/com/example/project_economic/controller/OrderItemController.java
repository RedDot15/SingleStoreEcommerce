package com.example.project_economic.controller;

import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.OrderItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/order-item")
public class OrderItemController {
    OrderItemService orderItemService;

    @GetMapping("/list/all/by/user/{userId}")
    public ResponseEntity<ResponseObject> showAll(@PathVariable Long userId){
        // Fetch & Return all users
        return buildResponse(
                HttpStatus.OK,
                "All users fetch successfully.",
                 orderItemService.getAllByUserId(userId)
        );
    }

    @PostMapping("/add/with/user/{userId}")
    public ResponseEntity<ResponseObject> addWithUserId(@PathVariable Long userId){
        return buildResponse(
                HttpStatus.OK,
                "Added order-items successfully.",
                orderItemService.addWithUserId(userId)
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
