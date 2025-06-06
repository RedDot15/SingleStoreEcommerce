package com.example.project_economic.controller;

import static com.example.project_economic.helper.ResponseBuilder.buildResponse;

import com.example.project_economic.dto.request.OrderItemRequest;
import com.example.project_economic.helper.ResponseObject;
import com.example.project_economic.service.OrderItemService;
import jakarta.validation.Valid;
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
	public ResponseEntity<ResponseObject> showAll(@PathVariable Long userId) {
		// Fetch & Return all users
		return buildResponse(HttpStatus.OK, "All users fetch successfully.", orderItemService.getAllByUserId(userId));
	}

	@PostMapping("/my-item/add")
	public ResponseEntity<ResponseObject> addMyItem(@Valid @RequestBody OrderItemRequest orderItemRequest) {
		return buildResponse(
				HttpStatus.OK, "Added order-items successfully.", orderItemService.addMyItem(orderItemRequest));
	}
}
