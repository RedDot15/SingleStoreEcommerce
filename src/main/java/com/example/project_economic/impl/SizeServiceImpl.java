package com.example.project_economic.impl;

import com.amazonaws.services.dlm.model.ResourceNotFoundException;
import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.SizeResponse;
import com.example.project_economic.entity.*;
import com.example.project_economic.exception.DuplicateException;
import com.example.project_economic.mapper.SizeMapper;
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
    public Set<SizeResponse> getActiveByProductId(Long productId) {
        // Product inactive exception
        productService.validateProductIsActive(productId);
        // Get product detail
        Set<ProductDetailEntity> productDetailEntitySet = productDetailRepository.findActiveByProductId(productId);
        // Get size
        Set<SizeEntity> sizeEntitySet = new TreeSet<>();
        for(ProductDetailEntity productDetailEntity : productDetailEntitySet){
            sizeEntitySet.add(productDetailEntity.getSizeEntity());
        }
        // Return result
        return new TreeSet<>(
                sizeEntitySet
                .stream().map(sizeMapper::toSizeResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public SizeResponse add(SizeRequest sizeRequest) {
        // Name duplicate exception
        if (sizeRepository.existsByName(sizeRequest.getName()))
            throw new DuplicateException("Size name duplicate.");
        // Add & Return
        return sizeMapper.toSizeResponse(
                sizeRepository.save(sizeMapper.toSizeEntity(sizeRequest))
        );
    }

    @Override
    public SizeResponse update(SizeRequest sizeRequest) {
        // Name duplicate exception
        if (sizeRepository.existsByNameExceptId(sizeRequest.getName(),sizeRequest.getId()))
            throw new DuplicateException("Size name duplicate.");
        // Get Entity & Not found/Deleted exception
        SizeEntity foundSizeEntity = sizeRepository.findById(sizeRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Size not found."));
        //Update
        sizeMapper.updateSizeEntityFromRequest(foundSizeEntity, sizeRequest);
        //Save
        return sizeMapper.toSizeResponse(sizeRepository.save(foundSizeEntity));
    }

    @Override
    public Long delete(Long id) {
        // Get entity
        SizeEntity foundSizeEntity = sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Size not found."));
        // Also delete every product detail relate to this size
        for (ProductDetailEntity productDetailEntity : foundSizeEntity.getProductDetailEntitySet()){
            productDetailService.delete(productDetailEntity.getId());
        }
        // Delete color
        sizeRepository.delete(foundSizeEntity);
        // Return ID
        return id;
    }
}
