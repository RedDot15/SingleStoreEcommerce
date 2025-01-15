package com.example.project_economic.repository;

import com.example.project_economic.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
    // Fetch
    @Query(value = "SELECT * FROM comment c WHERE c.product_id = :productId",nativeQuery = true)
    List<CommentEntity> findAllByProductId(Long productId);
}
