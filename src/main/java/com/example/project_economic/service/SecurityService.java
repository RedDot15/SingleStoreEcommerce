package com.example.project_economic.service;

public interface SecurityService {
	Boolean isCartItemOwner(Long cartItemId, Long userId);

	Boolean isCommentOwner(Long commentId, Long userId);
}
