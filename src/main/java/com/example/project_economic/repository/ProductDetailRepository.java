package com.example.project_economic.repository;

import com.example.project_economic.entity.ProductDetailEntity;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetailEntity, Long> {
  // Fetch
  @Query(
      value = "SELECT * FROM product_detail WHERE product_id = :productId AND is_deleted = false",
      nativeQuery = true)
  Set<ProductDetailEntity> findAllByProductId(@Param("productId") Long productId);

  @Query(
      "SELECT pd FROM ProductDetailEntity pd "
          + "WHERE pd.productEntity.id = :productId "
          + "AND pd.isActive = true AND pd.isDeleted = false "
          + "AND pd.productEntity.isActive = true "
          + "AND pd.productEntity.categoryEntity.isActive = true ")
  Set<ProductDetailEntity> findActiveByProductId(Long productId);

  // Fetch 1
  @Query(
      value =
          "SELECT * FROM product_detail WHERE product_id = :productId AND color_id = :colorId AND size_id = :sizeId AND is_deleted = false LIMIT 1",
      nativeQuery = true)
  ProductDetailEntity findFirstByProductIdAndColorIdAndSizeId(
      @Param("productId") Long productId,
      @Param("colorId") Long colorId,
      @Param("sizeId") Long sizeId);

  // Fetch 1 include deleted
  @Query("SELECT pd FROM ProductDetailEntity pd WHERE pd.id = :id")
  ProductDetailEntity findIncludingDeletedById(Long id);
}
