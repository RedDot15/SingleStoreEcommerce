package com.example.project_economic.dto.response;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse implements Comparable {
  Long id;

  String name;

  LocalDateTime createdDate;

  Boolean isActive;

  @Override
  public int compareTo(Object o) {
    return id.compareTo(((CategoryResponse) o).getId());
  }
}
