package com.example.project_economic.impl;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;
import com.example.project_economic.entity.CategoryEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductImageEntity;
import com.example.project_economic.mapper.CategoryMapper;
import com.example.project_economic.repository.CategoryRepository;
import com.example.project_economic.repository.ProductDetailRepository;
import com.example.project_economic.repository.ProductImageRepository;
import com.example.project_economic.repository.ProductRepository;
import com.example.project_economic.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    ProductRepository productRepository;
    ProductDetailRepository productDetailRepository;
    ProductImageRepository productImageRepository;

    @Override
    public Set<CategoryResponse> getAllForAdmin(){
        return new TreeSet<>(
                categoryRepository.findAll()
                .stream().map(categoryMapper::toSimpleCategoryResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Set<CategoryResponse> getAllActive() {
        return new TreeSet<>(
                categoryRepository.findAllActive()
                .stream().map(categoryMapper::toSimpleCategoryResponse)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public Boolean existsByNameExceptId(String name, Long id) {
        return categoryRepository.existsByNameExceptId(name, id);
    }

    @Override
    public CategoryResponse getFirstById(Long id) {
        return categoryMapper.toSimpleCategoryResponse(
                categoryRepository.findFirstById(id)
        );
    }

    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        return categoryMapper.toSimpleCategoryResponse(
                categoryRepository.save(categoryMapper.toCategoryEntity(categoryRequest))
        );
    }

    @Override
    public CategoryResponse update(CategoryRequest categoryRequest) {
        //Get old
        CategoryEntity foundCategoryEntity = categoryRepository.findFirstById(categoryRequest.getId());
        //Update
        categoryMapper.updateCategoryEntityFromRequest(foundCategoryEntity, categoryRequest);
        //Save
        return categoryMapper.toSimpleCategoryResponse(categoryRepository.save(foundCategoryEntity));
    }

    @Override
    public void delete(Long id) {
        //Delete every product of this category
        for (ProductEntity productEntity : productRepository.findAllByCategoryId(id)){
            //Also delete every product detail and product image of this product
            for (ProductDetailEntity productDetailEntity : productDetailRepository.findAllByProductId(productEntity.getId())){
                productDetailRepository.delete(productDetailEntity);
            }
            for (ProductImageEntity productImageEntity : productImageRepository.findAllByProductId(productEntity.getId())){
                productImageRepository.delete(productImageEntity);
            }
            //Delete product
            productRepository.delete(productEntity);
        }
        //Delete this category
        categoryRepository.deleteById(id);
    }

    @Override
    public Boolean activateCheck(Long id) {
        //Get Entity
        CategoryEntity foundCategoryEntity = categoryRepository.findFirstById(id);
        //Return false if category does not have at least 3 product
        if (foundCategoryEntity.getActiveProductEntitySet().size() < 3)
            return false;
        //Return true: activate valid
        return true;
    }

    @Override
    public CategoryResponse activate(Long id) {
        //Get old
        CategoryEntity oldCategoryEntity = categoryRepository.findFirstById(id);
        //Activate
        oldCategoryEntity.setIsActive(true);
        //Save & Return
        return categoryMapper.toSimpleCategoryResponse(
                categoryRepository.save(oldCategoryEntity)
        );
    }

    @Override
    public CategoryResponse deactivate(Long id) {
        //Get old
        CategoryEntity oldCategoryEntity = categoryRepository.findFirstById(id);
        //Deactivate
        oldCategoryEntity.setIsActive(false);
        //Save & Return
        return categoryMapper.toSimpleCategoryResponse(
                categoryRepository.save(oldCategoryEntity)
        );
    }
}
