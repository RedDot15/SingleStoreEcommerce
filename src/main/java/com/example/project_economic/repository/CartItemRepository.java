package com.example.project_economic.repository;

import com.example.project_economic.entity.CartItemEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
	// Fetch
	@Query(value = "SELECT * FROM cart_item c WHERE c.user_id = :userId", nativeQuery = true)
	List<CartItemEntity> findAllByUserId(@Param("userId") Long userId);

	@Query(
			value =
					"SELECT * FROM cart_item c WHERE c.user_id = :userId AND c.product_detail_id IN :productDetailIdList",
			nativeQuery = true)
	List<CartItemEntity> findAllByUserIdAndProductDetailIdIn(
			@Param("userId") Long userId, @Param("productDetailIdList") List<Long> productDetailIdList);

	@Query(
			value = "SELECT * FROM cart_item c WHERE c.user_id = :userId AND c.product_detail_id = :productDetailId",
			nativeQuery = true)
	CartItemEntity findFirstByUserIdAndProductDetailId(
			@Param("userId") Long userId, @Param("productDetailId") Long productDetailId);

	// Count
	@Query(value = "SELECT COUNT(c.id) FROM cart_item c WHERE c.user_id= :userId", nativeQuery = true)
	Long countAllByUserId(@Param("userId") Long userId);

	// Delete
	@Modifying
	@Query(
			value = "DELETE FROM cart_item ct WHERE ct.product_detail_id IN ( "
					+ "    SELECT pd.id FROM product_detail pd WHERE pd.product_id = :productId AND pd.is_active = true AND pd.is_deleted = false"
					+ ") ",
			nativeQuery = true)
	void deleteAllByProductId(Long productId);

	@Modifying
	@Query(value = "DELETE FROM CartItemEntity ct WHERE ct.productDetailEntity.id = :productDetailId")
	void deleteAllByProductDetailId(Long productDetailId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM cart_item c where c.user_id = :userId", nativeQuery = true)
	void deleteAllByUserId(@Param("userId") Long userId);
}
