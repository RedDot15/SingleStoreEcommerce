package com.example.project_economic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    Long id;

    String content;

    Integer star;

    Integer likeCount;

    Integer dislike;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    UserResponse userResponse;
}
