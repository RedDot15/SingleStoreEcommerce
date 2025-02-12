package com.example.project_economic.impl.authentication;

import com.example.project_economic.repository.CartItemRepository;
import com.example.project_economic.repository.OrderRepository;
import com.example.project_economic.repository.comment.CommentRepository;
import com.example.project_economic.service.authentication.SecurityService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
@Service("securityService")
public class SecurityServiceImpl implements SecurityService {
	CartItemRepository cartItemRepository;
	CommentRepository commentRepository;
	OrderRepository orderRepository;

	@Override
	public Boolean isCartItemOwner(Long cartItemId, Long userId) {
		return cartItemRepository
				.findById(cartItemId)
				.map(cartItem -> cartItem.getUserEntity().getId().equals(userId))
				.orElse(false);
	}

	@Override
	public Boolean isCommentOwner(Long commentId, Long userId) {
		return commentRepository
				.findById(commentId)
				.map(commentEntity -> commentEntity.getUserEntity().getId().equals(userId))
				.orElse(false);
	}

	@Override
	public Boolean isOrderOwner(String orderId, Long userId) {
		return orderRepository
				.findById(orderId)
				.map(orderEntity -> orderEntity.getUserEntity().getId().equals(userId))
				.orElse(false);
	}
}
