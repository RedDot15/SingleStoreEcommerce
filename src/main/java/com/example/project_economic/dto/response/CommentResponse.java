package com.example.project_economic.dto.response;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
