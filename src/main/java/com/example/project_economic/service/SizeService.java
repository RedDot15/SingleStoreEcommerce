package com.example.project_economic.service;


import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.SizeResponse;

import java.util.List;

public interface SizeService {
    List<SizeResponse> getAll();

    void save(SizeRequest sizeRequest);

    void deleteById(Long sizeId);

    List<SizeResponse> getAllSizeByProductId(Long productId);
}
