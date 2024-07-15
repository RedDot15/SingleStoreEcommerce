package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.response.PageProductResponse;
import com.example.project_economic.dto.response.ProductImageResponse;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.entity.CategoryEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.entity.ProductWithAttributesEntity;
import com.example.project_economic.mapper.ColorMapper;
import com.example.project_economic.mapper.ProductImageMapper;
import com.example.project_economic.mapper.ProductMapper;
import com.example.project_economic.mapper.SizeMapper;
import com.example.project_economic.repository.*;
import com.example.project_economic.service.CategoryService;
import com.example.project_economic.service.ProductService;
import com.example.project_economic.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ProductImageRepository productImageRepository;
    ProductImageMapper productImageMapper;
    ProductMapper productMapper;
    ColorRepository colorRepository;
    SizeRepository sizeRepository;
    CategoryRepository categoryRepository;

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(productMapper::toProductResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getAllIsActive() {
        return productRepository.findAllIsActive().stream().map(productMapper::toProductResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getAllIsActiveByPage(int pageSize, int pageNumber) {
        //Declare Pageable
        pageNumber -= 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());

        //Find all ACTIVE product id
        List<Long> idList = productRepository.findAllActiveId();
        //Find active product by page
        Page<ProductEntity> productEntityPage = productRepository.findAllActiveProductByPage(pageable, idList);

        return productEntityPage.stream().map(productMapper::toProductResponse).collect(Collectors.toList());
    }


    @Override
    public void add(ProductRequest productRequest) throws IOException {
        //Mapping productRequest -> productEntity
        ProductEntity productEntity = productMapper.toProductEntity(productRequest);
        productEntity.setCategoryEntity(
                categoryRepository.findById(productRequest.getCategoryId()).get()
        );
        List<ProductWithAttributesEntity> productWithAttributesEntityList = new ArrayList<>();
        for (Long colorId : productRequest.getColorIdList()){
            for (Long sizeId : productRequest.getSizeIdList()){
                productWithAttributesEntityList.add(ProductWithAttributesEntity.builder()
                        .productEntity(productEntity)
                        .colorEntity(colorRepository.findById(colorId).get())
                        .sizeEntity(sizeRepository.findById(sizeId).get())
                        .build());
            }
        }

        try {
            productRepository.save(productEntity);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        productEntity.setProductWithAttributesEntityList(productWithAttributesEntityList);

        List<ProductImageEntity> productImageEntityArrayList = new ArrayList<>();
        for (MultipartFile multipartFile : productRequest.getMultipartFileImageList()) {
            ProductImageEntity productImageEntity = productImageMapper.toProductImageEntity(multipartFile);
            productImageEntity.setProductEntity(productEntity);
            productImageEntityArrayList.add(productImageEntity);
        }
        productEntity.setProductImageEntityList(productImageEntityArrayList);

        return;
    }


    private ProductResponse enity_to_response(ProductEntity productSaved) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(productSaved.getId());
        productResponse.setName(productSaved.getName());
        productResponse.setDescription(productSaved.getDescription());
        productResponse.setCostPrice(productSaved.getCostPrice());
        productResponse.setSalePrice(productSaved.getSalePrice());
        productResponse.setLikes(productSaved.getLikes());
//        productResponse.setImage(productSaved.getImage());
        //url
        String url = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/products")
                .path("/get/")
                .path(productSaved.getId().toString())
                .toUriString();
//        productResponse.setImage_url(url);
//        productResponse.setImage_type(productSaved.getImage_type());
//        productResponse.setCategoryEntity(productSaved.getCategoryEntity());
//        productResponse.setIs_deleted(productSaved.getIs_deteted());
        productResponse.setIsActive(productSaved.getIsActive());
//        productResponse.setUserEntity(productSaved.getUserEntity());
        return productResponse;
    }

    @Override
    public ProductResponse update(ProductRequest productRequest, Long productId, MultipartFile file) {

        try {
            ProductEntity product = this.productRepository.findById(productId).get();
            if (file != null) {
//                product.setData(file.getBytes());
            }
            if (productRequest.getName() != null && !productRequest.getName().equals("")) {
                product.setName(productRequest.getName());
            }
            if (productRequest.getDescription() != null && !productRequest.getDescription().equals("")) {
                product.setDescription(productRequest.getDescription());
            }
            if (productRequest.getCostPrice() != null) {
                product.setCostPrice(productRequest.getCostPrice());
            }
            if (productRequest.getSalePrice() != null) {
                product.setSalePrice(productRequest.getSalePrice());
            }
//            if(productRequest.getCurrentQuantity()!=null){
//                product.setCurrentQuantity(productRequest.getCurrentQuantity());
//            }
            ProductEntity productSaved = this.productRepository.save(product);
            return this.enity_to_response(productSaved);
        } catch (Exception exception) {
            System.out.println("error update image");
        }
        return null;
    }

    @Override
    public ProductResponse getById(Long productId) {
        return productMapper.toProductResponse(productRepository.findById(productId).get());
    }

    @Override
    public void deleteById(Long productId) {
        ProductEntity product = this.productRepository.findById(productId).get();
        product.setIsActive(false);
        this.productRepository.save(product);
    }

    @Override
    public void activeById(Long productId) {
        ProductEntity product = this.productRepository.findById(productId).get();
        product.setIsActive(true);

        this.productRepository.save(product);
    }

    @Override
    public void likeById(Long productId) {
        ProductEntity product = this.productRepository.findById(productId).get();
        try {
            product.setLikes(product.getLikes() + 1);
        } catch (Exception e) {
            product.setLikes(1);
        }
        this.productRepository.save(product);
    }

    @Override
    public List<Object[]> countProductByCategoryId() {
        return this.productRepository.countProductByCategoryId();
    }

    @Override
    public ProductImageResponse findProductImageResponseById(Long id) {
        return productImageMapper.toProductImageResponse(productImageRepository.findById(id).get());
    }

    @Override
    public PageProductResponse findAllPagination(int pageNumber, int pageSize) {
        PageProductResponse pageProductResponse = new PageProductResponse();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        List<Long> ids = productRepository.findAllActiveId();
        Page<ProductEntity> productEntities = this.productRepository.findAllActiveProductByPage(pageable, ids);
        List<ProductResponse> productResponses = productEntities.stream()
                .map(product -> {
                    return this.enity_to_response(product);
                }).collect(Collectors.toList());
        int totalElement = (int) productEntities.getTotalElements();
        int lastPage = totalElement % pageSize == 0 ? totalElement / pageSize : (int) totalElement / pageSize + 1;
        if (lastPage == 0) lastPage = 1;
        pageProductResponse.setTotalPage(productEntities.getTotalPages() > 0 ? productEntities.getTotalPages() : 1);
        pageProductResponse.setCurrentPage(productEntities.getNumber());
        pageProductResponse.setLastPage(lastPage);
        pageProductResponse.setPageSize(pageSize);
        pageProductResponse.setProductResponses(productResponses);
        return pageProductResponse;
    }

    @Override
    public PageProductResponse findAllProductByKeyword(String keyword, Integer pageSize, Integer offsetNumber) {
        PageProductResponse pageProductResponse = new PageProductResponse();

        List<ProductEntity> productEntities = this.productRepository.findAllProductByKeyword('%' + keyword + '%', pageSize, offsetNumber);
        pageProductResponse.setProductResponses(
                productEntities.stream()
                        .map(product -> {
                            return this.enity_to_response(product);
                        }).collect(Collectors.toList())
        );
        int totalProduct = this.productRepository.countProductByKeyword('%' + keyword + '%');
        int totalPage = totalProduct % pageSize == 0 ? totalProduct / pageSize : (int) totalProduct / pageSize + 1;
        if (totalPage == 0) totalPage = 1;
        int currentPage = offsetNumber / pageSize + 1;
        int lastPage = totalPage;
        pageProductResponse.setTotalPage(totalPage);
        pageProductResponse.setCurrentPage(currentPage);
        pageProductResponse.setLastPage(lastPage);
        pageProductResponse.setPageSize(pageSize);
        return pageProductResponse;
    }

    @Override
    public PageProductResponse findAllProductByCategory(Long id, int pageSize, int offsetNumber) {
        PageProductResponse pageProductResponse = new PageProductResponse();
        List<ProductEntity> productEntities = this.productRepository.findAllProductByCategory(id, pageSize, offsetNumber);
        pageProductResponse.setProductResponses(
                productEntities.stream()
                        .map(product -> {
                            return this.enity_to_response(product);
                        }).collect(Collectors.toList())
        );
        int totalProduct = this.productRepository.countProductByCategory(id);
        int totalPage = totalProduct % pageSize == 0 ? totalProduct / pageSize : (int) totalProduct / pageSize + 1;
        if (totalPage == 0) totalPage = 1;
        int currentPage = offsetNumber / pageSize + 1;
        int lastPage = totalPage;
        pageProductResponse.setTotalPage(totalPage);
        pageProductResponse.setCurrentPage(currentPage);
        pageProductResponse.setLastPage(lastPage);
        pageProductResponse.setPageSize(pageSize);
        return pageProductResponse;
    }

    @Override
    public PageProductResponse findAllProductByCostPrice(int first_price, int second_price, int pageSize, int offsetNumber) {
        PageProductResponse pageProductResponse = new PageProductResponse();
        List<ProductEntity> productEntities = this.productRepository.findAllProductByPriceAndPagination(first_price, second_price, pageSize, offsetNumber);
        pageProductResponse.setProductResponses(
                productEntities.stream()
                        .map(product -> {
                            return this.enity_to_response(product);
                        }).collect(Collectors.toList())
        );
        int totalProduct = this.productRepository.countProductByPrice(first_price, second_price);
        int totalPage = totalProduct % pageSize == 0 ? totalProduct / pageSize : (int) totalProduct / pageSize + 1;
        if (totalPage == 0) totalPage = 1;
        int currentPage = offsetNumber / pageSize + 1;
        int lastPage = totalPage;
        pageProductResponse.setTotalPage(totalPage);
        pageProductResponse.setCurrentPage(currentPage);
        pageProductResponse.setLastPage(lastPage);
        pageProductResponse.setPageSize(pageSize);
        return pageProductResponse;
    }


}
