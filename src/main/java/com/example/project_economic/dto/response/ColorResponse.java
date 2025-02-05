package com.example.project_economic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColorResponse implements Comparable {
  Long id;

  String name;

  String hexCode;

  @Override
  public int compareTo(Object o) {
    return id.compareTo(((ColorResponse) o).getId());
  }
}
