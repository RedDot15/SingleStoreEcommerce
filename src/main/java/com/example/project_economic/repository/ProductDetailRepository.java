package com.example.project_economic.repository;

import com.example.project_economic.dto.request.ProductDetailRequest;
import com.example.project_economic.entity.ColorEntity;
import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.ProductDetailEntity;
import com.example.project_economic.entity.SizeEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetailEntity,Long> {
    @Query(value = "SELECT * FROM product_detail WHERE product_id = :productId AND is_deleted = false", nativeQuery = true)
    Set<ProductDetailEntity> findAllByProductId(Long productId);

    @Cacheable("productDetails")
    @Query(value = "SELECT * FROM product_detail WHERE id = :id LIMIT 1", nativeQuery = true)
    ProductDetailEntity findFirstById(Long id);

    @Query(value = "SELECT * FROM product_detail WHERE product_id = :productId AND color_id = :colorId AND size_id = :sizeId AND is_deleted = false LIMIT 1", nativeQuery = true)
    ProductDetailEntity findFirstByProductIdAndColorIdAndSizeId(Long productId, Long colorId, Long sizeId);
}
