package com.example.project_economic.dto.response.authentication;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshResponse {
  String accessToken;

  String refreshToken;
}
