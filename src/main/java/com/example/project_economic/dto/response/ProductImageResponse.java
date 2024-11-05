package com.example.project_economic.dto.response;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Lob;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageResponse implements Comparable{
    Long id;
    String name;

    ProductResponse productResponse;
    ColorResponse colorResponse;

    Boolean isActive;

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((ProductImageResponse)o).getId());
    }
}
