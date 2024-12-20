package com.example.project_economic.controller;

import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping(path = "/comment")
public class CommentController {
    CommentService commentService;

    @GetMapping("/list/all/by/product/{productId}")
    public ResponseEntity<ResponseObject> showAllCommentOfAProduct(@PathVariable Long productId){
        return buildResponse(
                HttpStatus.OK,
                "All comments fetch successfully.",
                commentService.getAllByProductId(productId)
        );
    }

    private ResponseEntity<ResponseObject> buildResponse(HttpStatus status, String message, Object data) {
        return ResponseEntity.status(status).body(
                new ResponseObject(
                        status.is2xxSuccessful() ? "ok" : "failed",
                        message,
                        data
                )
        );
    }
}
