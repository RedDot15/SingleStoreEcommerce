package com.example.project_economic.impl;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;
import com.example.project_economic.entity.CategoryEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.exception.ErrorCode;
import com.example.project_economic.exception.custom.AppException;
import com.example.project_economic.mapper.CategoryMapper;
import com.example.project_economic.repository.*;
import com.example.project_economic.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    ProductDetailRepository productDetailRepository;
    ProductImageRepository productImageRepository;
    CartItemRepository cartItemRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('GET_ALL_CATEGORY')")
    @Override
    public Set<CategoryResponse> getAll(){
        // Fetch
        List<CategoryEntity> categoryEntityList = categoryRepository.findAll();
        // Return result
        return new TreeSet<>(
                categoryEntityList
                .stream().map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Set<CategoryResponse> getActive() {
        // Fetch
        Set<CategoryEntity> categoryEntitySet = categoryRepository.findActive();
        // Return result
        return new TreeSet<>(
                categoryEntitySet
                .stream().map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toSet())
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_CATEGORY')")
    @Override
    public CategoryResponse add(CategoryRequest categoryRequest) {
        // Category name duplicate exception
        if (categoryRepository.existsByName(categoryRequest.getName())){
            throw new AppException(ErrorCode.CATEGORY_DUPLICATE);
        }
        // Add & Return
        return categoryMapper.toCategoryResponse(
                categoryRepository.save(categoryMapper.toCategoryEntity(categoryRequest))
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_CATEGORY')")
    @Override
    public CategoryResponse update(CategoryRequest categoryRequest) {
        // Name duplicate exception
        if (categoryRepository.existsByNameExceptId(categoryRequest.getName(), categoryRequest.getId()))
            throw new AppException(ErrorCode.CATEGORY_DUPLICATE);
        // Get Entity & Not found/Deleted exception
        CategoryEntity foundCategoryEntity = categoryRepository.findById(categoryRequest.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        // Update
        categoryMapper.updateCategoryEntityFromRequest(foundCategoryEntity, categoryRequest);
        // Save
        return categoryMapper.toCategoryResponse(categoryRepository.save(foundCategoryEntity));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_CATEGORY')")
    @Override
    public Long delete(Long id) {
        // Delete every product of this category
        for (ProductEntity productEntity : productRepository.findAllByCategoryId(id)){
            // Also delete every product detail and product image of this product
            productDetailRepository.deleteAll(productDetailRepository.findAllByProductId(productEntity.getId()));
            productImageRepository.deleteAll(productImageRepository.findAllByProductId(productEntity.getId()));
            // Delete this product from all user's cart
            cartItemRepository.deleteAllByProductId(productEntity.getId());
            // Delete product
            productRepository.delete(productEntity);
        }
        // Fetch and not found exception
        CategoryEntity foundCategoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        // Delete this category
        categoryRepository.delete(foundCategoryEntity);
        // Return id
        return id;
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ACTIVATE_CATEGORY')")
    @Override
    public CategoryResponse activate(Long id) {
        // Category not found/deleted exception
        CategoryEntity oldCategoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        // Check activation criteria
        if (oldCategoryEntity.getActiveProductEntitySet().size() < 3){
            throw new AppException(ErrorCode.CATEGORY_ACTIVATION_FAIL);
        }
        //Activate
        oldCategoryEntity.setIsActive(true);
        //Save & Return
        return categoryMapper.toCategoryResponse(
                categoryRepository.save(oldCategoryEntity)
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DEACTIVATE_CATEGORY')")
    @Override
    public CategoryResponse deactivate(Long id) {
        // Get old
        CategoryEntity oldCategoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        // Delete active product of this category from all user's cart
        for (ProductEntity productEntity : oldCategoryEntity.getActiveProductEntitySet()){
            cartItemRepository.deleteAllByProductId(productEntity.getId());
        }
        // Deactivate
        oldCategoryEntity.setIsActive(false);
        // Save & Return
        return categoryMapper.toCategoryResponse(
                categoryRepository.save(oldCategoryEntity)
        );
    }

    @Override
    public CategoryEntity validateCategoryIsActive(Long id) {
        // Deleted/Not found exception
        CategoryEntity foundCategoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        // Category inactive exception
        if (!foundCategoryEntity.getIsActive()) {
            throw new AppException(ErrorCode.CATEGORY_INACTIVE);
        }
        // Return
        return foundCategoryEntity;
    }
}
