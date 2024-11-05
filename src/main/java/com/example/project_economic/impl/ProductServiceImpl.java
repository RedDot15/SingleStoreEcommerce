package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.response.*;
import com.example.project_economic.entity.*;
import com.example.project_economic.mapper.*;
import com.example.project_economic.repository.*;
import com.example.project_economic.service.ProductService;
import com.example.project_economic.service.StorageService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ProductImageRepository productImageRepository;
    ProductMapper productMapper;
    ProductImageMapper productImageMapper;
    ProductDetailRepository productDetailRepository;
    ProductDetailMapper productDetailMapper;
    CategoryRepository categoryRepository;
    ColorRepository colorRepository;
    SizeRepository sizeRepository;
    StorageService storageService;

    @Override
    public Set<ProductResponse> getAllActive() {
        Set<ProductResponse> productResponseSet = productRepository.findAllActive()
                .stream().map(productMapper::toProductResponse)
                .collect(Collectors.toSet());
        for (ProductResponse productResponse : productResponseSet){
            productResponse.setColorResponseSet(new HashSet<>());
            productResponse.setSizeResponseSet(new HashSet<>());
            for (ProductDetailResponse productDetailResponse : productResponse.getActiveProductDetailResponseSet()){
                productResponse.getColorResponseSet().add(productDetailResponse.getColorResponse());
                productResponse.getSizeResponseSet().add(productDetailResponse.getSizeResponse());
            }
        }
        return productResponseSet;
    }

    @Override
    public Set<ProductResponse> getAllForAdmin() {
        Set<ProductResponse> productResponseSet = productRepository.findAll()
                .stream().map(productMapper::toProductAdminResponse)
                .collect(Collectors.toSet());
        return productResponseSet;
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
    public Boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    public Boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public Boolean existsByNameExceptId(String name, Long id) {
        return productRepository.existsByNameExceptId(name, id);
    }

    @Override
    public ProductResponse getFirstById(Long id) {
        return productMapper.toProductResponse(productRepository.findFirstById(id));
    }

    @Override
    public ProductResponse create(ProductRequest productRequest) {
        //Mapping
        ProductEntity newProductEntity = productMapper.toProductEntity(productRequest);
        //Set category
        newProductEntity.setCategoryEntity(categoryRepository.getReferenceById(productRequest.getCategoryId()));
        //Create
        return productMapper.toProductResponse(productRepository.save(newProductEntity));
    }

    @Override
    public ProductResponse update(ProductRequest productRequest) {
        //Get old
        ProductEntity foundProductEntity = productRepository.findFirstById(productRequest.getId());
        //Update
        productMapper.updateProductEntityFromRequest(foundProductEntity, productRequest);
        foundProductEntity.setCategoryEntity(categoryRepository.getReferenceById(productRequest.getCategoryId()));
        //Save
        return productMapper.toProductResponse(productRepository.save(foundProductEntity));
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
    public void delete(Long id) {
        //Get entity
        ProductEntity foundProductEntity = productRepository.findFirstById(id);
        CategoryEntity foundCategoryEntity = foundProductEntity.getCategoryEntity();
        //Delete every product detail and product image of this product
        for (ProductDetailEntity productDetailEntity : productDetailRepository.findAllByProductId(id)){
            productDetailRepository.delete(productDetailEntity);
        }
        for (ProductImageEntity productImageEntity : productImageRepository.findAllByProductId(id)){
            productImageRepository.delete(productImageEntity);
        }
        //Delete this product
        productRepository.delete(foundProductEntity);
        //Handle case if product already inactive
        if (!foundProductEntity.getIsActive())
            return;
        //Handle case if
        if (!foundCategoryEntity.getIsActive() || foundCategoryEntity.getActiveProductEntitySet().size() != 3)
            return;
        //Deactivate category (owner of this product)
        foundCategoryEntity.setIsActive(false);
        categoryRepository.save(foundCategoryEntity);
    }

    @Override
    public Boolean activateCheck(Long id) {
        //Get entity
        ProductEntity foundProductEntity = productRepository.findFirstById(id);
        //Return false if found product does not have at least 1 product detail or 1 image
        if (foundProductEntity.getActiveProductDetailEntitySet().size() == 0 ||
            foundProductEntity.getActiveProductImageEntitySet().size() == 0)
            return false;
        //Return true: valid
        return true;
    }

    @Override
    public ProductResponse activate(Long id) {
        //Get Entity
        ProductEntity productEntity = productRepository.findFirstById(id);
        //Activate
        productEntity.setIsActive(true);
        //Save & Return
        return productMapper.toProductResponse(
                productRepository.save(productEntity)
        );
    }

    @Override
    public String getDeactivateCheckMessage(Long id) {
        //Init message
        String msg = "";
        //Get entity
        ProductEntity foundProductEntity = productRepository.findFirstById(id);
        CategoryEntity foundCategoryEntity = foundProductEntity.getCategoryEntity();
        //Handle case this product is inactive
        if (!foundProductEntity.getIsActive())
            return "";
        //Return empty if category (owner of this product) already inactive
        // or it does not have at least 3 product
        if (!foundCategoryEntity.getIsActive() || foundCategoryEntity.getActiveProductEntitySet().size() != 3)
            return "";
        //Notify user their action may deactivate a category (owner of this product)
        msg += "This action will also deactivate category: " + foundCategoryEntity.getName() +
                " because it will no longer have at least 3 active product!";
        //Return message
        return msg;
    }

    @Override
    public ProductResponse deactivate(Long id) {
        //Get entity
        ProductEntity foundProductEntity = productRepository.findFirstById(id);
        CategoryEntity foundCategoryEntity = foundProductEntity.getCategoryEntity();
        //Deactivate this product
        foundProductEntity.setIsActive(false);
        productRepository.save(foundProductEntity);
        ProductResponse updatedProductResponse = productMapper.toProductResponse(foundProductEntity);
        //Return if category (owner of this product) already inactive
        // or it does not have at least 3 product
        if (!foundCategoryEntity.getIsActive() || foundCategoryEntity.getActiveProductEntitySet().size() != 3)
            return updatedProductResponse;
        //Deactivate category (owner of this product)
        foundCategoryEntity.setIsActive(false);
        categoryRepository.save(foundCategoryEntity);
        //Return updated
        return updatedProductResponse;
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
