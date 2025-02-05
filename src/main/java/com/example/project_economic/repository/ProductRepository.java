package com.example.project_economic.repository;

import com.example.project_economic.entity.ProductEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	// Fetch
	@Query(
			value = "" + "SELECT * FROM product " + "WHERE category_id = :categoryId AND is_deleted = false",
			nativeQuery = true)
	List<ProductEntity> findAllByCategoryId(@Param("categoryId") Long categoryId);

	@Query(
			value = ""
					+ "SELECT p FROM ProductEntity p "
					+ "WHERE p.categoryEntity.id = :categoryId "
					+ "AND p.isActive = true "
					+ "AND p.isDeleted = false "
					+ "AND p.categoryEntity.isActive = true")
	List<ProductEntity> findActiveByCategoryId(Long categoryId);

	@Query("SELECT p FROM ProductEntity p "
			+ "JOIN ProductDetailEntity pd ON pd.productEntity.id = p.id "
			+ "WHERE p.isActive = true "
			+ "AND (:categoryId IS NULL OR p.categoryEntity.id = :categoryId) AND p.categoryEntity.isActive = true "
			+ "AND (:fromPrice IS NULL OR :toPrice IS NULL OR p.salePrice BETWEEN :fromPrice AND :toPrice) "
			+ "AND pd.isActive = true AND pd.isDeleted = false "
			+ "AND (:colorId IS NULL OR pd.colorEntity.id = :colorId) AND (:sizeId IS NULL OR pd.sizeEntity.id = :sizeId) "
			+ "AND (:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%')) "
			+ "GROUP BY p")
	Page<ProductEntity> findActiveByFilter(
			Pageable pageable,
			Long categoryId,
			Double fromPrice,
			Double toPrice,
			Long colorId,
			Long sizeId,
			String keyword);

	// Exist
	@Query("SELECT EXISTS ("
			+ "   SELECT 1 FROM ProductEntity p"
			+ "   WHERE p.name = :name AND p.isDeleted = false"
			+ ")")
	Boolean existsByName(String name);

	@Query("SELECT EXISTS ("
			+ "   SELECT 1 FROM ProductEntity p"
			+ "   WHERE p.name = :name AND p.id <> :id AND p.isDeleted = false"
			+ ")")
	Boolean existsByNameExceptId(String name, Long id);

	// Fetch 1 include deleted
	@Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
	ProductEntity findIncludingDeletedById(Long id);
}
