package com.example.project_economic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse implements Comparable{
    Long id;

    String username;

    String role;

    String email;

    String phoneNumber;

    String address;

    Boolean isActive;

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((UserResponse)o).getId());
    }
}
