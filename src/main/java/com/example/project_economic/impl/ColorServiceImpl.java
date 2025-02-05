package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ColorResponse;
import com.example.project_economic.entity.ColorEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.ColorMapper;
import com.example.project_economic.repository.ColorRepository;
import com.example.project_economic.repository.ProductDetailRepository;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.service.ColorService;
import com.example.project_economic.service.ProductDetailService;
import com.example.project_economic.service.ProductImageService;
import com.example.project_economic.service.ProductService;
import jakarta.transaction.Transactional;
import java.util.HashSet;
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
public class ColorServiceImpl implements ColorService {
  ColorRepository colorRepository;
  ProductDetailRepository productDetailRepository;
  ProductRepository productRepository;
  ColorMapper colorMapper;
  ProductDetailService productDetailService;
  ProductImageService productImageService;
  ProductService productService;

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_COLOR')")
  @Override
  public Set<ColorResponse> getAll() {
    return new TreeSet<>(
        colorRepository.findAll().stream()
            .map(colorMapper::toColorResponse)
            .collect(Collectors.toSet()));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_COLOR')")
  @Override
  public Set<ColorResponse> getAllByProductId(Long productId) {
    // Product not found/deleted exception
    productRepository
        .findById(productId)
        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    // Get product detail
    Set<ProductDetailEntity> productDetailEntitySet =
        productDetailRepository.findAllByProductId(productId);
    // Get color
    Set<ColorEntity> colorEntitySet = new HashSet<>();
    for (ProductDetailEntity productDetailEntity : productDetailEntitySet) {
      colorEntitySet.add(productDetailEntity.getColorEntity());
    }
    // Return result
    return new TreeSet<>(
        colorEntitySet.stream().map(colorMapper::toColorResponse).collect(Collectors.toSet()));
  }

  @Override
  public Set<ColorResponse> getActiveByProductId(Long productId) {
    // Product inactive exception
    productService.validateProductIsActive(productId);
    // Get product detail
    Set<ProductDetailEntity> productDetailEntitySet =
        productDetailRepository.findActiveByProductId(productId);
    // Get color
    Set<ColorEntity> colorEntitySet = new TreeSet<>();
    for (ProductDetailEntity productDetailEntity : productDetailEntitySet) {
      colorEntitySet.add(productDetailEntity.getColorEntity());
    }
    // Return result
    return new TreeSet<>(
        colorEntitySet.stream().map(colorMapper::toColorResponse).collect(Collectors.toSet()));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_COLOR')")
  @Override
  public ColorResponse add(ColorRequest colorRequest) {
    // Name or hexcode duplicate exception
    if (colorRepository.existsByNameOrHexCode(colorRequest.getName(), colorRequest.getHexCode()))
      throw new AppException(ErrorCode.COLOR_DUPLICATE);
    // Add & Return
    return colorMapper.toColorResponse(
        colorRepository.save(colorMapper.toColorEntity(colorRequest)));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_COLOR')")
  @Override
  public ColorResponse update(ColorRequest colorRequest) {
    // Name duplicate exception
    if (colorRepository.existsByNameOrHexCodeExceptId(
        colorRequest.getName(), colorRequest.getHexCode(), colorRequest.getId()))
      throw new AppException(ErrorCode.COLOR_DUPLICATE);
    // Get Entity & Not found/Deleted exception
    ColorEntity foundColorEntity =
        colorRepository
            .findById(colorRequest.getId())
            .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));
    // Update
    colorMapper.updateColorEntityFromRequest(foundColorEntity, colorRequest);
    // Save
    return colorMapper.toColorResponse(colorRepository.save(foundColorEntity));
  }

  @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_COLOR')")
  @Override
  public Long delete(Long id) {
    // Get entity
    ColorEntity foundColorEntity =
        colorRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_FOUND));
    // Also delete every product detail relate to this color
    for (ProductDetailEntity productDetailEntity : foundColorEntity.getProductDetailEntitySet()) {
      productDetailService.delete(productDetailEntity.getId());
    }
    // Also delete every product image relate to this color
    for (ProductImageEntity productImageEntity : foundColorEntity.getProductImageEntitySet()) {
      productImageService.delete(productImageEntity.getId());
    }
    // Delete color
    colorRepository.delete(foundColorEntity);
    // Return ID
    return id;
  }
}
