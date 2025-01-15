package com.example.project_economic.repository;

import com.example.project_economic.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    Boolean existsByName(String name);

    @Query("SELECT p FROM PermissionEntity p WHERE p.name = :name AND p.id <> :id")
    Boolean existsByNameExceptId(String name, Long id);
}
