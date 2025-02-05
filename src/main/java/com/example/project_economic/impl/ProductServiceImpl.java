package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ProductRequest;
import com.example.project_economic.dto.request.filter.ProductFilterRequest;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.entity.CategoryEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.ProductMapper;
import com.example.project_economic.repository.*;
import com.example.project_economic.service.CategoryService;
import com.example.project_economic.service.ProductService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class ProductServiceImpl implements ProductService {
  ProductRepository productRepository;
  ProductImageRepository productImageRepository;
  ProductDetailRepository productDetailRepository;
  CategoryRepository categoryRepository;
  CartItemRepository cartItemRepository;
  ProductMapper productMapper;
  CategoryService categoryService;

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_PRODUCT')")
  @Override
  public Set<ProductResponse> getAll() {
    // Find all products
    List<ProductEntity> productEntityList = productRepository.findAll();
    // Return result
    return productEntityList.stream()
        .map(productMapper::toProductResponse)
        .collect(Collectors.toCollection(TreeSet::new));
  }

  @Override
  public Set<ProductResponse> getActiveByCategoryId(Long categoryId) {
    // Validate category
    categoryService.validateCategoryIsActive(categoryId);
    // Find active products by categoryId
    List<ProductEntity> productEntityList = productRepository.findActiveByCategoryId(categoryId);
    // Return result
    return productEntityList.stream()
        .map(productMapper::toProductResponse)
        .collect(Collectors.toCollection(TreeSet::new));
  }

  @Override
  public Page<ProductResponse> getActiveByFilter(
      Integer pageNumber, Integer pageSize, ProductFilterRequest productFilterRequest) {
    // Category inactive exception
    if (productFilterRequest.getCategoryId() != null)
      categoryService.validateCategoryIsActive(productFilterRequest.getCategoryId());
    // Declare Pageable
    Pageable pageable = getPageable(pageNumber, pageSize);
    // Find active product by page
    Page<ProductEntity> productEntityPage =
        productRepository.findActiveByFilter(
            pageable,
            productFilterRequest.getCategoryId(),
            productFilterRequest.getFromPrice(),
            productFilterRequest.getToPrice(),
            productFilterRequest.getColorId(),
            productFilterRequest.getSizeId(),
            productFilterRequest.getKeyword());
    // Return result
    return productEntityPage.map(productMapper::toProductResponse);
  }

  @Override
  public ProductResponse getActiveById(Long id) {
    // Product inactive exception
    ProductEntity productEntity = validateProductIsActive(id);
    // Return result
    return productMapper.toProductResponse(productEntity);
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_PRODUCT')")
  @Override
  public ProductResponse add(ProductRequest productRequest) {
    // Name duplicate exception
    if (productRepository.existsByName(productRequest.getName()))
      throw new AppException(ErrorCode.PRODUCT_DUPLICATE);
    // Mapping
    ProductEntity newProductEntity = productMapper.toProductEntity(productRequest);
    newProductEntity.setCategoryEntity(
        categoryRepository.getReferenceById(productRequest.getCategoryId()));
    // Add & Return
    return productMapper.toProductResponse(productRepository.save(newProductEntity));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PRODUCT')")
  @Override
  public ProductResponse update(ProductRequest productRequest) {
    // Name duplicate exception
    if (productRepository.existsByNameExceptId(productRequest.getName(), productRequest.getId())) {
      throw new AppException(ErrorCode.PRODUCT_DUPLICATE);
    }
    // Get old
    ProductEntity foundProductEntity =
        productRepository
            .findById(productRequest.getId())
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    // Update
    productMapper.updateProductEntityFromRequest(foundProductEntity, productRequest);
    foundProductEntity.setCategoryEntity(
        categoryRepository.getReferenceById(productRequest.getCategoryId()));
    // Save
    return productMapper.toProductResponse(productRepository.save(foundProductEntity));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT')")
  @Override
  public Long delete(Long id) {
    // Get entity
    ProductEntity foundProductEntity =
        productRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    CategoryEntity foundCategoryEntity = foundProductEntity.getCategoryEntity();
    // Delete this product from all user's cart
    cartItemRepository.deleteAllByProductId(id);
    // Delete every product detail and product image of this product
    productDetailRepository.deleteAll(productDetailRepository.findAllByProductId(id));
    productImageRepository.deleteAll(productImageRepository.findAllByProductId(id));
    // Delete this product
    productRepository.delete(foundProductEntity);
    // Handle case if product already inactive
    if (!foundProductEntity.getIsActive()) return id;
    // Handle case if category already inactive or don't need to deactivate due to still appropriate
    if (!foundCategoryEntity.getIsActive()
        || foundCategoryEntity.getActiveProductEntitySet().size() != 3) return id;
    // Deactivate category (owner of this product)
    categoryService.deactivate(foundCategoryEntity.getId());
    // Return id
    return id;
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('ACTIVATE_PRODUCT')")
  @Override
  public ProductResponse activate(Long id) {
    // Product deleted/not found exception
    ProductEntity foundProductEntity =
        productRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    // Check activation criteria
    if (foundProductEntity.getActiveProductDetailEntitySet().isEmpty()
        || foundProductEntity.getActiveProductImageEntitySet().isEmpty()) {
      throw new AppException(ErrorCode.PRODUCT_ACTIVATION_FAIL);
    }
    // Activate
    foundProductEntity.setIsActive(true);
    // Save & Return
    return productMapper.toProductResponse(productRepository.save(foundProductEntity));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('DEACTIVATE_PRODUCT')")
  @Override
  public String getDeactivateCheckMessage(Long id) {
    // Init message
    String msg = "This action will remove this product from all user's cart<br/>";
    // Get entity
    ProductEntity foundProductEntity =
        productRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    CategoryEntity foundCategoryEntity = foundProductEntity.getCategoryEntity();
    // Handle case this product is inactive
    if (!foundProductEntity.getIsActive()) return msg;
    // Return empty if category (owner of this product) already inactive,
    // or it does not have at least 3 product
    if (!foundCategoryEntity.getIsActive()
        || foundCategoryEntity.getActiveProductEntitySet().size() != 3) return msg;
    // Notify user their action may deactivate a category (owner of this product)
    msg +=
        "This action will also deactivate category: "
            + foundCategoryEntity.getName()
            + " because it will no longer have at least 3 active product!";
    // Return message
    return msg;
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('DEACTIVATE_PRODUCT')")
  @Override
  public ProductResponse deactivate(Long id) {
    // Get entity
    ProductEntity foundProductEntity =
        productRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    CategoryEntity foundCategoryEntity = foundProductEntity.getCategoryEntity();
    // Delete this product from all user's cart
    cartItemRepository.deleteAllByProductId(id);
    // Deactivate this product
    foundProductEntity.setIsActive(false);
    productRepository.save(foundProductEntity);
    ProductResponse updatedProductResponse = productMapper.toProductResponse(foundProductEntity);
    /* Return if category (owner of this product) already inactive,
    or it does not have at least 3 product */
    if (!foundCategoryEntity.getIsActive()
        || foundCategoryEntity.getActiveProductEntitySet().size() != 3)
      return updatedProductResponse;
    // Deactivate category (owner of this product)
    categoryService.deactivate(foundCategoryEntity.getId());
    // Return updated
    return updatedProductResponse;
  }

  @Override
  public ProductResponse like(Long id) {
    // Fetch
    ProductEntity foundProductEntity = validateProductIsActive(id);
    // Update number of likes
    foundProductEntity.setLikes(foundProductEntity.getLikes() + 1);
    // Save and Return
    return productMapper.toProductResponse(productRepository.save(foundProductEntity));
  }

  public ProductEntity validateProductIsActive(Long id) {
    // Product not found/deleted exception
    ProductEntity foundProductEntity =
        productRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    // Product inactive exception
    if (!foundProductEntity.getIsActive()) {
      throw new AppException(ErrorCode.PRODUCT_INACTIVE);
    }
    // Category inactive exception
    if (!foundProductEntity.getCategoryEntity().getIsActive()) {
      throw new AppException(ErrorCode.CATEGORY_INACTIVE);
    }
    // Return
    return foundProductEntity;
  }

  private Pageable getPageable(Integer pageNumber, Integer pageSize) {
    int defaultPageNumber = 0;
    int defaultSize = Integer.MAX_VALUE;
    if (pageNumber == null || pageSize == null) {
      return PageRequest.of(defaultPageNumber, defaultSize, Sort.by("id").ascending());
    }
    return PageRequest.of(pageNumber - 1, pageSize, Sort.by("id").ascending());
  }
}
