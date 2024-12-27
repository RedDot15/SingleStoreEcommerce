package com.example.project_economic.impl;

import com.amazonaws.services.dlm.model.ResourceNotFoundException;
import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ColorResponse;
import com.example.project_economic.entity.ColorEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.exception.custom.DuplicateException;
import com.example.project_economic.mapper.ColorMapper;
import com.example.project_economic.repository.ColorRepository;
import com.example.project_economic.repository.ProductDetailRepository;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.service.ColorService;
import com.example.project_economic.service.ProductDetailService;
import com.example.project_economic.service.ProductImageService;
import com.example.project_economic.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class ColorServiceImpl implements ColorService {
    ColorRepository colorRepository;
    ColorMapper colorMapper;
    ProductDetailRepository productDetailRepository;
    ProductDetailService productDetailService;
    ProductImageService productImageService;
    ProductService productService;
    ProductRepository productRepository;

    @Override
    public Set<ColorResponse> getAll() {
        return new TreeSet<>(
                colorRepository.findAll()
                .stream().map(colorMapper::toColorResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Set<ColorResponse> getAllByProductId(Long productId) {
        // Product not found/deleted exception
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
        // Get product detail
        Set<ProductDetailEntity> productDetailEntitySet = productDetailRepository.findAllByProductId(productId);
        // Get color
        Set<ColorEntity> colorEntitySet = new HashSet<>();
        for (ProductDetailEntity productDetailEntity : productDetailEntitySet){
            colorEntitySet.add(productDetailEntity.getColorEntity());
        }
        // Return result
        return new TreeSet<>(
                colorEntitySet
                .stream().map(colorMapper::toColorResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Set<ColorResponse> getActiveByProductId(Long productId) {
        // Product inactive exception
        productService.validateProductIsActive(productId);
        // Get product detail
        Set<ProductDetailEntity> productDetailEntitySet = productDetailRepository.findActiveByProductId(productId);
        // Get color
        Set<ColorEntity> colorEntitySet = new TreeSet<>();
        for(ProductDetailEntity productDetailEntity : productDetailEntitySet){
            colorEntitySet.add(productDetailEntity.getColorEntity());
        }
        // Return result
        return new TreeSet<>(
                colorEntitySet
                .stream().map(colorMapper::toColorResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public ColorResponse add(ColorRequest colorRequest) {
        // Name or hexcode duplicate exception
        if (colorRepository.existsByNameOrHexCode(colorRequest.getName(),colorRequest.getHexCode()))
            throw new DuplicateException("Color name or hexcode duplicate.");
        // Add & Return
        return colorMapper.toColorResponse(
                colorRepository.save(colorMapper.toColorEntity(colorRequest))
        );
    }

    @Override
    public ColorResponse update(ColorRequest colorRequest) {
        // Name duplicate exception
        if (colorRepository.existsByNameOrHexCodeExceptId(colorRequest.getName(),colorRequest.getHexCode(),colorRequest.getId()))
            throw new DuplicateException("Color name duplicate.");
        // Get Entity & Not found/Deleted exception
        ColorEntity foundColorEntity = colorRepository.findById(colorRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Color not found."));
        // Update
        colorMapper.updateColorEntityFromRequest(foundColorEntity, colorRequest);
        // Save
        return colorMapper.toColorResponse(colorRepository.save(foundColorEntity));
    }

    @Override
    public Long delete(Long id) {
        // Get entity
        ColorEntity foundColorEntity = colorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Color not found."));
        // Also delete every product detail relate to this color
        for (ProductDetailEntity productDetailEntity : foundColorEntity.getProductDetailEntitySet()){
            productDetailService.delete(productDetailEntity.getId());
        }
        // Also delete every product image relate to this color
        for (ProductImageEntity productImageEntity : foundColorEntity.getProductImageEntitySet()){
            productImageService.delete(productImageEntity.getId());
        }
        // Delete color
        colorRepository.delete(foundColorEntity);
        // Return ID
        return id;
    }

}
