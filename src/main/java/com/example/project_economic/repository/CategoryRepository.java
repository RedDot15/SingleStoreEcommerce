package com.example.project_economic.repository;

import com.example.project_economic.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    @Query(value = "SELECT * FROM category c WHERE c.is_active = true AND c.is_deleted = false",nativeQuery = true)
    Set<CategoryEntity> findAllActive();

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM CategoryEntity c" +
            "   WHERE c.id = :id AND c.isDeleted = false" +
            ")")
    boolean existsById(Long id);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM CategoryEntity c" +
            "   WHERE c.name = :name AND c.isDeleted = false" +
            ")")
    Boolean existsByName(String name);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM CategoryEntity c " +
            "   WHERE c.name = :name AND c.id <> :id AND c.isDeleted = false" +
            ")")
    Boolean existsByNameExceptId(String name, Long id);

    CategoryEntity findFirstById(Long categoryId);

}
