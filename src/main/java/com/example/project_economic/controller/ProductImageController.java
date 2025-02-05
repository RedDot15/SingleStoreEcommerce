package com.example.project_economic.controller;

import static com.example.project_economic.helper.ResponseBuilder.buildResponse;

import com.example.project_economic.dto.request.ProductImageRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.ProductImageService;
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
@RequestMapping("/product-image")
public class ProductImageController {
	ProductImageService productImageService;

	@GetMapping("/list/all/by/product/{productId}")
	public ResponseEntity<ResponseObject> showAllProductImageOfAProduct(@PathVariable Long productId) {
		// Return all products-image by productId
		return buildResponse(
				HttpStatus.OK,
				"All product-images fetch successfully.",
				productImageService.getAllByProductId(productId));
	}

	@GetMapping("/get/by/product/{productId}/color/{colorId}")
	public ResponseEntity<ResponseObject> showActiveByProductIdAndColorId(
			@PathVariable Long productId, @PathVariable Long colorId) {
		// Return active color by productId
		return buildResponse(
				HttpStatus.OK,
				"Active product-image fetch successfully.",
				productImageService.getActiveByProductIdAndColorId(productId, colorId));
	}

	@PostMapping("/add")
	public ResponseEntity<ResponseObject> add(
			@Validated({Create.class, Default.class}) @ModelAttribute ProductImageRequest productImageRequest) {
		// Add product-image & Return
		return buildResponse(
				HttpStatus.OK, "Created new product image successfully.", productImageService.add(productImageRequest));
	}

	@PutMapping("/update")
	public ResponseEntity<ResponseObject> update(
			@Validated({Update.class, Default.class}) @ModelAttribute ProductImageRequest productImageRequest) {
		// Update & Return product image
		return buildResponse(
				HttpStatus.OK, "Updated product image successfully.", productImageService.update(productImageRequest));
	}

	@DeleteMapping("/{productImageId}/delete")
	public ResponseEntity<ResponseObject> delete(@PathVariable Long productImageId) {
		// Delete product-detail & Return
		return buildResponse(
				HttpStatus.OK, "Deleted product-image successfully.", productImageService.delete(productImageId));
	}

	@PutMapping("/{productImageId}/activate")
	public ResponseEntity<ResponseObject> activate(@PathVariable Long productImageId) {
		// Activate & Return
		return buildResponse(
				HttpStatus.OK, "Activated product-image successfully.", productImageService.activate(productImageId));
	}

	@GetMapping("/{productImageId}/deactivate/check")
	public ResponseEntity<ResponseObject> deactivateCheck(@PathVariable Long productImageId) {
		// Return check message
		return buildResponse(
				HttpStatus.OK,
				"Deactivate check message fetch successfully.",
				productImageService.getDeactivateCheckMessage(productImageId));
	}

	@PutMapping("/{productImageId}/deactivate")
	public ResponseEntity<ResponseObject> deactivate(@PathVariable Long productImageId) {
		// Deactivate & Return
		return buildResponse(
				HttpStatus.OK,
				"Deactivated product-image successfully.",
				productImageService.deactivate(productImageId));
	}
}
