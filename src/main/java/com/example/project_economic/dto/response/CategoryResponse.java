package com.example.project_economic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse implements Comparable{
    Long id;

    String name;

    LocalDateTime createdDate;

    Boolean isActive;

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((CategoryResponse)o).getId());
    }
}
