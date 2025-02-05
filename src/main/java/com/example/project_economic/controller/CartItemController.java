package com.example.project_economic.controller;

import static com.example.project_economic.helper.ResponseBuilder.buildResponse;

import com.example.project_economic.dto.request.CartItemRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.CartItemService;
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
@RequestMapping(path = "/cart-item")
public class CartItemController {
  CartItemService cartItemService;

  @GetMapping("/list/all/by/user/{userId}")
  public ResponseEntity<ResponseObject> showAllCartItemOfAnUser(@PathVariable Long userId) {
    // Fetch & Return all cart-item by userId
    return buildResponse(
        HttpStatus.OK, "All cart-item fetch successfully.", cartItemService.getAllByUserId(userId));
  }

  @GetMapping("/cart-size/get/by/user/{userId}")
  public ResponseEntity<ResponseObject> showCartSizeOfAnUser(@PathVariable Long userId) {
    // Fetch & Return cart-size by userId
    return buildResponse(
        HttpStatus.OK, "Cart-size fetch successfully.", cartItemService.countAllByUserId(userId));
  }

  @PostMapping("/add")
  public ResponseEntity<ResponseObject> add(
      @Validated({Create.class, Default.class}) @RequestBody CartItemRequest cartItemRequest) {
    return buildResponse(
        HttpStatus.OK, "Added cart-item successfully.", cartItemService.add(cartItemRequest));
  }

  @PutMapping("/update")
  public ResponseEntity<ResponseObject> update(
      @Validated({Update.class, Default.class}) @RequestBody CartItemRequest cartItemRequest) {
    return buildResponse(
        HttpStatus.OK, "Updated cart-item successfully.", cartItemService.update(cartItemRequest));
  }

  @DeleteMapping("/{cartItemId}/delete")
  public ResponseEntity<ResponseObject> delete(@PathVariable Long cartItemId) {
    return buildResponse(
        HttpStatus.OK, "Deleted cart-item successfully", cartItemService.delete(cartItemId));
  }

  @DeleteMapping("/delete/by/user/{userId}")
  public ResponseEntity<ResponseObject> deleteAllByUserId(@PathVariable Long userId) {
    return buildResponse(
        HttpStatus.OK,
        "Deleted cart-items successfully",
        cartItemService.deleteAllByUserId(userId));
  }
}
