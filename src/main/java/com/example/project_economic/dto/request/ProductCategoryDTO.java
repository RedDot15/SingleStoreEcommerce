package com.example.project_economic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductCategoryDTO {
    private String name;
    private int quantity;
}
