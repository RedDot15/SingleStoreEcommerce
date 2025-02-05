package com.example.project_economic.service;

import com.example.project_economic.dto.request.CommentRequest;
import com.example.project_economic.dto.response.CommentResponse;
import java.util.List;

public interface CommentService {
	// Fetch
	List<CommentResponse> getAllByProductId(Long productId);

	// Add/Edit/Delete
	CommentResponse add(CommentRequest commentRequest);

	CommentResponse edit(CommentRequest commentRequest);

	Long delete(Long id);
}
