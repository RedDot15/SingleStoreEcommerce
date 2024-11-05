package com.example.project_economic.dto.response;

import com.example.project_economic.entity.ProductEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse implements Comparable{
    Long id;
    String name;
    String createdDate;
    Boolean isActive;

    Set<ProductResponse> activeProductResponseSet;

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((CategoryResponse)o).getId());
    }
}
