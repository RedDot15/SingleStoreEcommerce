package com.example.project_economic.repository;

import com.example.project_economic.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity,Long> {
    // Fetch
    @Query(value = "SELECT * FROM order_item oi WHERE oi.order_id IN (" +
                   "    SELECT * FROM order o WHERE o.user_id = :userId" +
                   ")", nativeQuery = true)
    List<OrderItemEntity> findAllByUserId(@Param("userId") Long userId);
}
