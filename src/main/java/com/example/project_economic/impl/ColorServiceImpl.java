package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ColorResponse;
import com.example.project_economic.entity.ColorEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.mapper.ColorMapper;
import com.example.project_economic.repository.ColorRepository;
import com.example.project_economic.repository.ProductDetailRepository;
import com.example.project_economic.service.ColorService;
import com.example.project_economic.service.ProductDetailService;
import com.example.project_economic.service.ProductImageService;
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

    @Override
    public Set<ColorResponse> getAll() {
        return new TreeSet<>(
                colorRepository.findAll()
                .stream().map(colorMapper::toColorResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Boolean existsById(Long id) {
        return colorRepository.existsById(id);
    }

    @Override
    public Boolean existsByNameOrHexCode(String name, String hexCode) {
        return colorRepository.existsByNameOrHexCode(name, hexCode);
    }

    @Override
    public Boolean existsByNameOrHexCodeExceptId(String name, String hexCode, Long id) {
        return colorRepository.existsByNameOrHexCodeExceptId(name, hexCode, id);
    }

    @Override
    public ColorResponse create(ColorRequest colorRequest) {
        return colorMapper.toColorResponse(
                colorRepository.save(colorMapper.toColorEntity(colorRequest))
        );
    }

    @Override
    public ColorResponse update(ColorRequest colorRequest) {
        //Get old
        ColorEntity foundColorEntity = colorRepository.findFirstById(colorRequest.getId());
        //Update
        colorMapper.updateColorEntityFromRequest(foundColorEntity, colorRequest);
        //Save
        return colorMapper.toColorResponse(colorRepository.save(foundColorEntity));
    }

    @Override
    public void delete(Long id) {
        //Get entity
        ColorEntity foundColorEntity = colorRepository.findFirstById(id);
        //Also delete every product detail relate to this color
        for (ProductDetailEntity productDetailEntity : foundColorEntity.getProductDetailEntitySet()){
            productDetailService.delete(productDetailEntity.getId());
        }
        //Also delete every product image relate to this color
        for (ProductImageEntity productImageEntity : foundColorEntity.getProductImageEntitySet()){
            productImageService.delete(productImageEntity.getId());
        }
        //Delete color
        colorRepository.delete(foundColorEntity);
        return;
    }

    @Override
    public Set<ColorResponse> getAllColorOfAProduct(Long productId) {
        Set<ProductDetailEntity> productDetailEntitySet = productDetailRepository.findAllByProductId(productId);

        Set<ColorEntity> colorEntitySet = new HashSet<>();
        for (ProductDetailEntity productDetailEntity : productDetailEntitySet){
            colorEntitySet.add(productDetailEntity.getColorEntity());
        }

        return new TreeSet<>(colorEntitySet.stream().map(colorMapper::toColorResponse).collect(Collectors.toSet()));
    }
}
