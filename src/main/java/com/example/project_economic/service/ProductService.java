package com.example.project_economic.service;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.response.PageProductResponse;
import com.example.project_economic.dto.response.ProductImageResponse;
import com.example.project_economic.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ProductService {
    Set<ProductResponse> getAllActive();

    Set<ProductResponse> getAllForAdmin();

    List<ProductResponse> getAllIsActiveByPage(int pageSize, int pageNumber);

    Boolean existsById(Long id);

    Boolean existsByName(String name);

    Boolean existsByNameExceptId(String name, Long id);

    ProductResponse getFirstById(Long id);

    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(ProductRequest productRequest);

    void delete(Long id);

    Boolean activateCheck(Long id);

    ProductResponse activate(Long id);

    String getDeactivateCheckMessage(Long id);

    ProductResponse deactivate(Long id);

    void likeById(Long productId);

    List<Object[]> countProductByCategoryId();

    ProductImageResponse findProductImageResponseById(Long id);

    PageProductResponse findAllPagination(int pageNumber, int pageSize);

    PageProductResponse findAllProductByKeyword(String keyword, Integer pageSize, Integer offsetNumber);

    PageProductResponse findAllProductByCategory(Long id, int pageSize, int pageNumber);

    PageProductResponse findAllProductByCostPrice(int first_price, int second_price, int pageSize, int offsetNumber);

}
