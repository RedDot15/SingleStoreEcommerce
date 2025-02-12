package com.example.project_economic.service.authentication;

public interface SecurityService {
	Boolean isCartItemOwner(Long cartItemId, Long userId);

	Boolean isCommentOwner(Long commentId, Long userId);

	Boolean isOrderOwner(String orderId, Long userId);
}
