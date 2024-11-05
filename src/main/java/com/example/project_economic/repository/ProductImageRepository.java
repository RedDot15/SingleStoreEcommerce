package com.example.project_economic.repository;

import com.example.project_economic.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity,Long> {
    @Query(value = "SELECT * FROM product_image WHERE product_id = :productId AND is_deleted = false", nativeQuery = true)
    Set<ProductImageEntity> findAllByProductId(Long productId);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM ProductImageEntity pm" +
            "   WHERE pm.productEntity.id = :productId AND pm.colorEntity.id = :colorId AND pm.isDeleted = false" +
            ")")
    Boolean existsByProductIdAndColorId(Long productId, Long colorId);

    ProductImageEntity findFirstById(Long id);

    @Query(value = "SELECT * FROM product_image WHERE product_id = :productId AND color_id = :colorId LIMIT 1", nativeQuery = true)
    ProductImageEntity findFirstByProductIdAndColorId(Long productId, Long colorId);
}
