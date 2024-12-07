package com.example.project_economic.impl;

import com.amazonaws.services.dlm.model.ResourceNotFoundException;
import com.example.project_economic.dto.request.CommentRequest;
import com.example.project_economic.dto.response.CommentResponse;
import com.example.project_economic.entity.CommentEntity;
import com.example.project_economic.mapper.CommentMapper;
import com.example.project_economic.repository.CommentRepository;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.repository.UserRepository;
import com.example.project_economic.service.CommentService;
import com.example.project_economic.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class CommentServiceImpl implements CommentService {
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    ProductService productService;
    UserRepository userRepository;
    ProductRepository productRepository;

    @Override
    public List<CommentResponse> getAllByProductId(Long productId) {
        // Validate product is active
        productService.validateProductIsActive(productId);
        // Fetch & Return comments
        return commentRepository.findAllByProductId(productId)
                .stream().map(commentMapper::toCommentResponse)
                .collect(Collectors.toList());
    }

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
        return commentMapper.toCommentResponse(
                commentRepository.save(newCommentEntity)
        );
    }

    @Override
    public CommentResponse edit(CommentRequest commentRequest) {
        // Fetch old
        CommentEntity foundCommentEntity = commentRepository.findById(commentRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
        // Update comment
        foundCommentEntity.setContent(commentRequest.getContent());
        foundCommentEntity.setStar(commentRequest.getStar());
        foundCommentEntity.setUpdatedAt(LocalDateTime.now());
        // Save & Return
        return commentMapper.toCommentResponse(
                commentRepository.save(foundCommentEntity)
        );
    }

    @Override
    public Long delete(Long id) {
        // Fetch
        CommentEntity foundCommentEntity = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
        // Delete
        commentRepository.delete(foundCommentEntity);
        // Return ID
        return id;
    }
}
