package com.example.project_economic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountProuductDto {
    private Long count;
    private Long categoryId;
}
