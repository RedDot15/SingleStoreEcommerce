package com.example.project_economic.dto.response;

import com.example.project_economic.entity.ProductEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Long id;
    String name;
    String createdDate;
    Boolean isActive;

    List<ProductEntity> productEntityList;
}
