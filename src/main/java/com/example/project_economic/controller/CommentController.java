package com.example.project_economic.controller;

import static com.example.project_economic.helper.ResponseBuilder.buildResponse;

import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/comment")
public class CommentController {
	CommentService commentService;

	@GetMapping("/list/all/by/product/{productId}")
	public ResponseEntity<ResponseObject> showAllCommentOfAProduct(@PathVariable Long productId) {
		return buildResponse(
				HttpStatus.OK, "All comments fetch successfully.", commentService.getAllByProductId(productId));
	}
}
