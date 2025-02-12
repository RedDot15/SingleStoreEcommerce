package com.example.project_economic.impl.comment;

import com.example.project_economic.dto.request.comment.CommentRequest;
import com.example.project_economic.dto.response.comment.CommentResponse;
import com.example.project_economic.entity.comment.CommentEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.comment.CommentMapper;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.repository.comment.CommentRepository;
import com.example.project_economic.service.ProductService;
import com.example.project_economic.service.comment.CommentService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class CommentServiceImpl implements CommentService {
	CommentRepository commentRepository;
	UserRepository userRepository;
	ProductRepository productRepository;
	CommentMapper commentMapper;
	ProductService productService;

	@Override
	public List<CommentResponse> getAllByProductId(Long productId) {
		// Validate product is active
		productService.validateProductIsActive(productId);
		// Fetch & Return comments
		return commentRepository.findAllByProductId(productId).stream()
				.map(commentMapper::toCommentResponse)
				.collect(Collectors.toList());
	}

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('ADD_COMMENT') or #commentRequest.userId == authentication.principal.claims['uid']")
	@Override
	public CommentResponse add(CommentRequest commentRequest) {
		// Create new comment
		CommentEntity newCommentEntity = CommentEntity.builder()
				.content(commentRequest.getContent())
				.star(commentRequest.getStar())
				.userEntity(userRepository.getReferenceById(commentRequest.getUserId()))
				.productEntity(productRepository.getReferenceById(commentRequest.getProductId()))
				.build();
		// Save & Return
		return commentMapper.toCommentResponse(commentRepository.save(newCommentEntity));
	}

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('UPDATE_COMMENT') or @securityService.isCommentOwner(#commentRequest.id, authentication.principal.claims['uid'])")
	@Override
	public CommentResponse edit(CommentRequest commentRequest) {
		// Fetch old
		CommentEntity foundCommentEntity = commentRepository
				.findById(commentRequest.getId())
				.orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
		// Update comment
		foundCommentEntity.setContent(commentRequest.getContent());
		foundCommentEntity.setStar(commentRequest.getStar());
		foundCommentEntity.setUpdatedAt(LocalDateTime.now());
		// Save & Return
		return commentMapper.toCommentResponse(commentRepository.save(foundCommentEntity));
	}

	@PreAuthorize(
			"hasRole('ADMIN') or hasAuthority('DELETE_COMMENT') or @securityService.isCommentOwner(#id, authentication.principal.claims['uid'])")
	@Override
	public Long delete(Long id) {
		// Fetch
		CommentEntity foundCommentEntity =
				commentRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
		// Delete
		commentRepository.delete(foundCommentEntity);
		// Return ID
		return id;
	}
}
