package com.example.project_economic.repository;

import com.example.project_economic.entity.RoleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
	Boolean existsByName(String name);

	@Query("SELECT r FROM RoleEntity r WHERE r.name = :name AND r.id <> :id")
	Boolean existsByNameExceptId(String name, Long id);

	Optional<RoleEntity> findByName(String name);
}
