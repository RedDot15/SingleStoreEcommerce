package com.example.project_economic.impl;

import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.ProductResponse;
import com.example.project_economic.dto.response.SizeResponse;
import com.example.project_economic.entity.ColorEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductWithAttributesEntity;
import com.example.project_economic.entity.SizeEntity;
import com.example.project_economic.mapper.SizeMapper;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.repository.ProductWithAttributesRepository;
import com.example.project_economic.repository.SizeRepository;
import com.example.project_economic.service.ProductService;
import com.example.project_economic.service.SizeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SizeServiceImpl implements SizeService {
    SizeRepository sizeRepository;
    SizeMapper sizeMapper;
    ProductService productService;
    ProductRepository productRepository;
    ProductWithAttributesRepository productWithAttributesRepository;

    @Override
    public List<SizeResponse> getAll() {
        //Find all sizeEntity in database
        //Mapping sizeEntityList to sizeResponseList then return
        return sizeRepository.findAll().stream().map(sizeMapper::toSizeResponse).collect(Collectors.toList());
    }

    @Override
    public void save(SizeRequest sizeRequest) {
        //Mapping sizeRequest -> sizeEntity
        //Adding/Update sizeEntity to database
        sizeRepository.save(sizeMapper.toSizeEntity(sizeRequest));
        return;
    }

    @Override
    public void deleteById(Long sizeId) {
        //Delete selected sizeEntity by id
        sizeRepository.deleteById(sizeId);
        return;
    }

    @Override
    public List<SizeResponse> getAllSizeByProductId(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId).get();
        Set<ProductWithAttributesEntity> productWithAttributesEntitySet = productWithAttributesRepository.findAllByProductEntity(productEntity);
        Set<SizeEntity> sizeEntitySet = new HashSet<>();
        for (ProductWithAttributesEntity productWithAttributesEntity : productWithAttributesEntitySet){
            sizeEntitySet.add(productWithAttributesEntity.getSizeEntity());
        }
        return sizeEntitySet.stream().map(sizeMapper::toSizeResponse).collect(Collectors.toList());
    }
}
