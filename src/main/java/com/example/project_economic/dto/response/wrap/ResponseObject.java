package com.example.project_economic.dto.response.wrap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject {
    private String status;

    private String message;

    private Object data;
}
