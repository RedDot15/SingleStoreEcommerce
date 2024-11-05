package com.example.project_economic.impl;

import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.SizeResponse;
import com.example.project_economic.entity.*;
import com.example.project_economic.mapper.SizeMapper;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.repository.ProductDetailRepository;
import com.example.project_economic.repository.SizeRepository;
import com.example.project_economic.service.ProductDetailService;
import com.example.project_economic.service.ProductService;
import com.example.project_economic.service.SizeService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class SizeServiceImpl implements SizeService {
    SizeRepository sizeRepository;
    SizeMapper sizeMapper;
    ProductService productService;
    ProductRepository productRepository;
    ProductDetailRepository productDetailRepository;
    ProductDetailService productDetailService;

    @Override
    public Set<SizeResponse> getAll() {
        return new TreeSet<>(
                sizeRepository.findAll()
                .stream().map(sizeMapper::toSizeResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Set<SizeResponse> getAllSizeByProductId(Long productId) {
        Boolean exist = productRepository.existsById(productId);
        if (exist){
            Set<ProductDetailEntity> productDetailEntitySet = productDetailRepository.findAllByProductId(productId);
            Set<SizeEntity> sizeEntitySet = new HashSet<>();
            for (ProductDetailEntity productDetailEntity : productDetailEntitySet){
                sizeEntitySet.add(productDetailEntity.getSizeEntity());
            }
            return sizeEntitySet.stream().map(sizeMapper::toSizeResponse).collect(Collectors.toSet());
        }
        else{
            return null;
        }
    }

    @Override
    public Boolean existsById(Long id) {
        return sizeRepository.existsById(id);
    }

    @Override
    public Boolean existsByName(String name) {
        return sizeRepository.existsByName(name);
    }

    @Override
    public Boolean existsByNameExceptId(String name, Long id) {
        return sizeRepository.existsByNameExceptId(name,id);
    }

    @Override
    public SizeResponse create(SizeRequest sizeRequest) {
        return sizeMapper.toSizeResponse(
                sizeRepository.save(sizeMapper.toSizeEntity(sizeRequest))
        );
    }

    @Override
    public SizeResponse update(SizeRequest sizeRequest) {
        //Get old
        SizeEntity foundSizeEntity = sizeRepository.findFirstById(sizeRequest.getId());
        //Update
        sizeMapper.updateSizeEntityFromRequest(foundSizeEntity, sizeRequest);
        //Save
        return sizeMapper.toSizeResponse(sizeRepository.save(foundSizeEntity));
    }

    @Override
    public void delete(Long id) {
        //Get entity
        SizeEntity foundSizeEntity = sizeRepository.findFirstById(id);
        //Also delete every product detail relate to this size
        for (ProductDetailEntity productDetailEntity : foundSizeEntity.getProductDetailEntitySet()){
            productDetailService.delete(productDetailEntity.getId());
        }
        //Delete color
        sizeRepository.delete(foundSizeEntity);
        return;
    }

}
