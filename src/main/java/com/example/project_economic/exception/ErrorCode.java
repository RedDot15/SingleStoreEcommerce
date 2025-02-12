package com.example.project_economic.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
	// General
	UNCATEGORIZED(HttpStatus.INTERNAL_SERVER_ERROR, "Uncategorized error."),
	// Category
	CATEGORY_DUPLICATE(HttpStatus.CONFLICT, "Category name already exists."),
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found."),
	CATEGORY_INACTIVE(HttpStatus.BAD_REQUEST, "Category inactive."),
	CATEGORY_ACTIVATION_FAIL(
			HttpStatus.UNPROCESSABLE_ENTITY, "This category does not have at least 3 active products to activate."),
	// Product
	PRODUCT_DUPLICATE(HttpStatus.CONFLICT, "Product name already exists."),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found."),
	PRODUCT_INACTIVE(HttpStatus.BAD_REQUEST, "Product inactive."),
	PRODUCT_ACTIVATION_FAIL(
			HttpStatus.UNPROCESSABLE_ENTITY, "This category does not have at least 3 active products to activate."),
	// Product detail
	PRODUCT_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "Product detail not found."),
	// Product image
	PRODUCT_IMAGE_DUPLICATE(HttpStatus.CONFLICT, "Product Image already exists for this color."),
	PRODUCT_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Product Image not found."),
	// Color
	COLOR_DUPLICATE(HttpStatus.CONFLICT, "Color name or hexcode already exists."),
	COLOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Color not found."),
	// Size
	SIZE_DUPLICATE(HttpStatus.CONFLICT, "Size name already exists."),
	SIZE_NOT_FOUND(HttpStatus.NOT_FOUND, "Size not found."),
	// User
	USERNAME_DUPLICATE(HttpStatus.CONFLICT, "Username already exists."),
	EMAIL_DUPLICATE(HttpStatus.CONFLICT, "Email already exists."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found."),
	WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "Wrong password."),
	// Comment
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not found."),
	// Cart item
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart-item not found."),
	// Order
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found."),
	ORDER_PRICE_MISMATCH(HttpStatus.CONFLICT, "Order price mismatch."),
	ORDER_EMPTY(HttpStatus.CONFLICT, "Order is empty."),
	ORDER_COMPLETED(HttpStatus.CONFLICT, "Order is completed."),
	// Authentication
	UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Unauthenticated error."),
	UNAUTHORIZED(HttpStatus.FORBIDDEN, "You do not have permission to perform this operation."),
	// Role
	ROLE_DUPLICATE(HttpStatus.CONFLICT, "Role name already exists."),
	ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role not found."),
	// Permission
	PERMISSION_DUPLICATE(HttpStatus.CONFLICT, "Permission name already exists."),
	PERMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Permission not found."),
	// Storage
	EMPTY_IMAGE_FILE(HttpStatus.BAD_REQUEST, "Empty image file."),
	INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "You can only upload image file."),
	FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "File must be <= 5MB."),
	FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed."),
	// Payment
	PAYMENT_CREATE_REQUEST_FAILED(
			HttpStatus.INTERNAL_SERVER_ERROR, "Payment create request failed due to an unexpected error."),
	PAYMENT_EXCUTE_REQUEST_FAILED(
			HttpStatus.INTERNAL_SERVER_ERROR, "Payment excute request failed due to an unexpected error."),
	;

	HttpStatus httpStatus;
	String message;
}
