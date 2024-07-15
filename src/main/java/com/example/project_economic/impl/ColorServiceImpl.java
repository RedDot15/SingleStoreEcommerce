package com.example.project_economic.impl;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ColorResponse;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.entity.ColorEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductWithAttributesEntity;
import com.example.project_economic.mapper.ColorMapper;
import com.example.project_economic.repository.ColorRepository;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.repository.ProductWithAttributesRepository;
import com.example.project_economic.service.ColorService;
import com.example.project_economic.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//This annotation create a bean for final attribute so no need for @Autowired
@RequiredArgsConstructor
//This annotation set default access level to private and final
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ColorServiceImpl implements ColorService {
    ColorRepository colorRepository;
    ColorMapper colorMapper;
    ProductService productService;
    ProductWithAttributesRepository productWithAttributesRepository;
    ProductRepository productRepository;

    @Override
    public List<ColorResponse> getAll() {
        //find all colorEntity in database
        //Mapping colorEntityList to colorResponseList then return
        return colorRepository.findAll().stream().map(colorMapper::toColorResponse).collect(Collectors.toList());
    }

    @Override
    public void save(ColorRequest colorRequest) {
        //Mapping colorRequest -> colorEntity
        //Save/Update colorEntity to database
        colorRepository.save(colorMapper.toColorEntity(colorRequest));
        return;
    }

    @Override
    public void deleteById(Long colorId) {
        //Delete selected colorEntity by id
        colorRepository.deleteById(colorId);
        return;
    }

    @Override
    public List<ColorResponse> getAllColorOfAProduct(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId).get();
        Set<ProductWithAttributesEntity> productWithAttributesEntitySet = productWithAttributesRepository.findAllByProductEntity(productEntity);
        Set<ColorEntity> colorEntitySet = new HashSet<>();
        for (ProductWithAttributesEntity productWithAttributesEntity : productWithAttributesEntitySet){
            colorEntitySet.add(productWithAttributesEntity.getColorEntity());
        }
        return colorEntitySet.stream().map(colorMapper::toColorResponse).collect(Collectors.toList());
    }
}
