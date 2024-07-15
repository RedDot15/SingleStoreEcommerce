package com.example.project_economic.service;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.response.PageProductResponse;
import com.example.project_economic.dto.response.ProductImageResponse;
import com.example.project_economic.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAll();

    List<ProductResponse> getAllIsActive();

    List<ProductResponse> getAllIsActiveByPage(int pageSize, int pageNumber);

    void add(ProductRequest productRequest) throws IOException;

    ProductResponse update(ProductRequest productRequest, Long productId, MultipartFile file);

    ProductResponse getById(Long productId);

    void deleteById(Long productId);

    void activeById(Long productId);

    void likeById(Long productId);


    List<Object[]> countProductByCategoryId();

    ProductImageResponse findProductImageResponseById(Long id);

    PageProductResponse findAllPagination(int pageNumber, int pageSize);

    PageProductResponse findAllProductByKeyword(String keyword, Integer pageSize, Integer offsetNumber);

    PageProductResponse findAllProductByCategory(Long id, int pageSize, int pageNumber);

    PageProductResponse findAllProductByCostPrice(int first_price, int second_price, int pageSize, int offsetNumber);

}
