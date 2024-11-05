package com.example.project_economic.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    Long id;
    String username;
    String password;
    String role;
    String email;
    String phoneNumber;
    String address;
}
