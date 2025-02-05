package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ProductDetailRequest;
import com.example.project_economic.dto.response.ProductDetailResponse;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.ProductDetailMapper;
import com.example.project_economic.repository.*;
import com.example.project_economic.service.ProductDetailService;
import com.example.project_economic.service.ProductService;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class ProductDetailServiceImpl implements ProductDetailService {
  ProductDetailRepository productDetailRepository;
  ProductRepository productRepository;
  ColorRepository colorRepository;
  SizeRepository sizeRepository;
  CartItemRepository cartItemRepository;
  ProductDetailMapper productDetailMapper;
  ProductService productService;

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_PRODUCT_DETAIL')")
  @Override
  public Set<ProductDetailResponse> getAllByProductId(Long productId) {
    // Product not found/deleted exception
    productRepository
        .findById(productId)
        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    // Find all product-detail by product ID
    Set<ProductDetailEntity> productDetailEntitySet =
        productDetailRepository.findAllByProductId(productId);
    // Return result
    return new TreeSet<>(
        productDetailEntitySet.stream()
            .map(productDetailMapper::toProductDetailResponse)
            .collect(Collectors.toSet()));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_PRODUCT_DETAIL')")
  @Override
  public ProductDetailResponse add(ProductDetailRequest productDetailRequest) {
    // Check if a matching ProductDetail exists
    ProductDetailEntity foundProductDetailEntity =
        productDetailRepository.findFirstByProductIdAndColorIdAndSizeId(
            productDetailRequest.getProductId(),
            productDetailRequest.getColorId(),
            productDetailRequest.getSizeId());
    if (foundProductDetailEntity != null) {
      // Calculate up new stock
      foundProductDetailEntity.setStock(
          foundProductDetailEntity.getStock() + productDetailRequest.getStock());
      // Save & Map to response and set the new flag as false
      ProductDetailResponse productDetailResponse =
          productDetailMapper.toProductDetailResponse(
              productDetailRepository.save(foundProductDetailEntity));
      productDetailResponse.setIsNew(false);
      // Return result
      return productDetailResponse;
    }
    // Create new product-detail
    ProductDetailEntity newProductDetailEntity =
        ProductDetailEntity.builder()
            .productEntity(productRepository.getReferenceById(productDetailRequest.getProductId()))
            .colorEntity(colorRepository.getReferenceById(productDetailRequest.getColorId()))
            .sizeEntity(sizeRepository.getReferenceById(productDetailRequest.getSizeId()))
            .stock(productDetailRequest.getStock())
            .build();
    // Save & Map to response and set the new flag as true
    ProductDetailResponse productDetailResponse =
        productDetailMapper.toProductDetailResponse(
            productDetailRepository.save(newProductDetailEntity));
    productDetailResponse.setIsNew(true);
    // Return result
    return productDetailResponse;
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PRODUCT_DETAIL')")
  @Override
  public ProductDetailResponse update(ProductDetailRequest productDetailRequest) {
    // Get old & Not found/Deleted exception
    ProductDetailEntity oldProductDetailEntity =
        productDetailRepository
            .findById(productDetailRequest.getId())
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));
    // Update
    oldProductDetailEntity.setStock(productDetailRequest.getStock());
    // Save & Return
    return productDetailMapper.toProductDetailResponse(
        productDetailRepository.save(oldProductDetailEntity));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT_DETAIL')")
  @Override
  public Long delete(Long id) {
    // Get entity
    ProductDetailEntity foundProductDetailEntity =
        productDetailRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));
    ProductEntity foundProductEntity = foundProductDetailEntity.getProductEntity();
    // Delete this product detail from all user's cart
    cartItemRepository.deleteAllByProductDetailId(id);
    // Delete this product detail
    productDetailRepository.deleteById(id);
    // if product detail inactive then product (owner of this product detail) wont be affect
    if (!foundProductDetailEntity.getIsActive()) return id;
    // Handle case product (owner of this product detail) inactive or still having at least 1
    // product detail
    if (!foundProductEntity.getIsActive()
        || foundProductEntity.getActiveProductDetailEntitySet().size() != 1) return id;
    // Deactivate product (owner of this product detail)
    productService.deactivate(foundProductEntity.getId());
    // Return id
    return id;
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('ACTIVATE_PRODUCT_DETAIL')")
  @Override
  public ProductDetailResponse activate(Long id) {
    // Get Entity
    ProductDetailEntity foundProductDetailEntity =
        productDetailRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));
    // Activate
    foundProductDetailEntity.setIsActive(true);
    // Save & Return
    return productDetailMapper.toProductDetailResponse(
        productDetailRepository.save(foundProductDetailEntity));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('DEACTIVATE_PRODUCT_DETAIL')")
  @Override
  public String getDeactivateCheckMessage(Long id) {
    // Init message
    String msg = "This action will delete this product detail from all user's cart\n";
    // Get Entity
    ProductDetailEntity foundProductDetailEntity =
        productDetailRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));
    ProductEntity foundProductEntity = foundProductDetailEntity.getProductEntity();
    // Handle case product detail already inactive
    if (!foundProductDetailEntity.getIsActive()) {
      return msg;
    }
    /* Return empty if product already inactive or product still have
    at least one active product detail after deactivate this product */
    if (!foundProductEntity.getIsActive()
        || foundProductEntity.getActiveProductDetailEntitySet().size() != 1) return msg;
    // Notify user their action will also deactivate a product
    msg +=
        "This action will also deactivate product: "
            + foundProductEntity.getName()
            + " because it will no longer have at least 1 active color & size<br/>";
    msg += productService.getDeactivateCheckMessage(foundProductEntity.getId());
    // Return message
    return msg;
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('DEACTIVATE_PRODUCT_DETAIL')")
  @Override
  public ProductDetailResponse deactivate(Long id) {
    // Get Entity
    ProductDetailEntity foundProductDetailEntity =
        productDetailRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_DETAIL_NOT_FOUND));
    ProductEntity foundProductEntity = foundProductDetailEntity.getProductEntity();
    // Remove this product-detail from all user's cart
    cartItemRepository.deleteAllByProductDetailId(id);
    // Deactivate this product detail
    foundProductDetailEntity.setIsActive(false);
    productDetailRepository.save(foundProductDetailEntity);
    ProductDetailResponse updatedProductDetailResponse =
        productDetailMapper.toProductDetailResponse(foundProductDetailEntity);
    /* Return if product (owner of this product detail) already inactive or product
    still have at least one active product detail after deactivate this product */
    if (!foundProductEntity.getIsActive()
        || foundProductEntity.getActiveProductDetailEntitySet().size() != 1)
      return updatedProductDetailResponse;
    // Deactivate product (owner of this product detail)
    productService.deactivate(foundProductEntity.getId());
    // Return updated
    return updatedProductDetailResponse;
  }
}
