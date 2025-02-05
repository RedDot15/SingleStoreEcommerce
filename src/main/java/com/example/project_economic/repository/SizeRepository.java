package com.example.project_economic.repository;

import com.example.project_economic.entity.SizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<SizeEntity, Long> {
	// Exist
	@Query("SELECT EXISTS ("
			+ "   SELECT 1 FROM SizeEntity s"
			+ "   WHERE s.name = :name AND s.isDeleted = false"
			+ ")")
	Boolean existsByName(String name);

	@Query("SELECT EXISTS ("
			+ "   SELECT 1 FROM SizeEntity s"
			+ "   WHERE s.name = :name AND s.id <> :id AND s.isDeleted = false"
			+ ")")
	Boolean existsByNameExceptId(String name, Long id);

	// Fetch 1 include deleted
	@Query("SELECT s FROM SizeEntity s WHERE s.id = :id")
	SizeEntity findIncludingDeletedById(Long id);
}
