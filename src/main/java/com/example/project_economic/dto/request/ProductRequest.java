package com.example.project_economic.dto.request;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    Long id;
    String name;
    String description;
    Long costPrice;
    Long salePrice;
    Integer likes;
    Boolean isActive;

    Long categoryId;
    List<Long> colorIdList;
    List<Long> sizeIdList;

    List<MultipartFile> multipartFileImageList;

    public ProductRequest() {
        this.likes = 0;
        this.isActive = true;
    }
}
