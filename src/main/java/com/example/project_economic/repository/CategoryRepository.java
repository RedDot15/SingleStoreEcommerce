package com.example.project_economic.repository;

import com.example.project_economic.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    // Fetch
    @Query(value = "SELECT * FROM category c WHERE c.is_active = true AND c.is_deleted = false",nativeQuery = true)
    Set<CategoryEntity> findActive();

    // Exist
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

    // Fetch 1 include deleted
    @Query("SELECT c FROM CategoryEntity c WHERE c.id = :id")
    CategoryEntity findIncludingDeletedById(Long id);
}
