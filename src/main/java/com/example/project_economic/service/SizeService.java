package com.example.project_economic.service;


import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.SizeResponse;
import org.hibernate.engine.jdbc.Size;

import java.util.List;
import java.util.Set;

public interface SizeService {
    Set<SizeResponse> getAll();

    Set<SizeResponse> getAllSizeByProductId(Long productId);

    Boolean existsById(Long id);

    Boolean existsByName(String name);

    Boolean existsByNameExceptId(String name, Long id);

    SizeResponse create(SizeRequest sizeRequest);

    SizeResponse update(SizeRequest sizeRequest);

    void delete(Long sizeId);

}
