package com.example.project_economic.repository;

import com.example.project_economic.entity.ColorEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductWithAttributesEntity;
import com.example.project_economic.entity.SizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductWithAttributesRepository extends JpaRepository<ProductWithAttributesEntity,Long> {
    ProductWithAttributesEntity findFirstByProductEntityAndColorEntityAndSizeEntity(ProductEntity productEntity, ColorEntity colorEntity, SizeEntity sizeEntity);

    Set<ProductWithAttributesEntity> findAllByProductEntity(ProductEntity productEntity);
}
