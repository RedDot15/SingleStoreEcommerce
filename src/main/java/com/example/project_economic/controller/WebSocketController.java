package com.example.project_economic.controller;

import com.example.project_economic.dto.request.CommentRequest;
import com.example.project_economic.dto.response.wrap.ResponseObject;
import com.example.project_economic.service.CommentService;
import com.example.project_economic.validation.group.Create;
import com.example.project_economic.validation.group.Update;
import jakarta.validation.groups.Default;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class WebSocketController {
    CommentService commentService;

    @MessageMapping("/send/new-comment")
    @SendTo("/topic/new-comment")
    public ResponseEntity<ResponseObject> handleCommentAddRequest(@Validated({Create.class, Default.class}) @RequestBody CommentRequest commentRequest){
        return buildResponse(
                HttpStatus.OK,
                "New comment added.",
                commentService.add(commentRequest)
        );
    }

    @MessageMapping("/send/edit-comment")
    @SendTo("/topic/edit-comment")
    public ResponseEntity<ResponseObject> handleCommentEditRequest(@Validated({Update.class,Default.class}) @RequestBody CommentRequest commentRequest){
        return buildResponse(
                HttpStatus.OK,
                "A comment is edited.",
                commentService.edit(commentRequest)
        );
    }

    @MessageMapping("/send/delete-comment/{commentId}")
    @SendTo("/topic/delete-comment")
    public ResponseEntity<ResponseObject> handleCommentDeleteRequest(@PathVariable Long commentId){
        return buildResponse(
                HttpStatus.OK,
                "A comment is deleted.",
                commentService.delete(commentId)
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
