package com.example.project_economic.impl;

import com.example.project_economic.dto.request.CategoryRequest;
import com.example.project_economic.dto.response.CategoryResponse;
import com.example.project_economic.entity.CategoryEntity;
import com.example.project_economic.mapper.CategoryMapper;
import com.example.project_economic.repository.CategoryRepository;
import com.example.project_economic.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(categoryMapper::toCategoryResponse).collect(Collectors.toList());
    }


    @Override
    public void save(CategoryRequest categoryRequest) {
        CategoryEntity categoryEntity;
        //In case Adding new category
        if (categoryRequest.getId() == null) {
            categoryEntity = new CategoryEntity(categoryRequest.getName());
        }
        //In case Updating category
        else {
            categoryEntity = categoryMapper.toCategoryEntity(categoryRequest);
        }
        categoryRepository.save(categoryEntity);
        return;
    }

    @Override
    public CategoryEntity findById(Long id) {
        return categoryRepository.findById(id).get();
    }

    @Override
    public void deleteById(Long categoryId) {
        //Delete selected categoryEntity by id
        categoryRepository.deleteById(categoryId);
    }

//    @Override
//    public void enableById(Long id) {
//        CategoryEntity category = categoryRepository.getById(id);
//        category.set_actived(true);
////        category.updateTime();
//        categoryRepository.save(category);
//    }

    @Override
    public List<CategoryEntity> findAllByActived() {
        return this.categoryRepository.findByActived();
    }

    @Override
    public List<CategoryRequest> getCategoryAndProduct() {
        return null;
    }
}
