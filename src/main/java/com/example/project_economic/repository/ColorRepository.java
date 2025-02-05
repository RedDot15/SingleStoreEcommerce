package com.example.project_economic.repository;

import com.example.project_economic.entity.ColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ColorRepository extends JpaRepository<ColorEntity, Long> {
	// Exist
	@Query("SELECT EXISTS ("
			+ "   SELECT 1 FROM ColorEntity c"
			+ "   WHERE (c.name = :name OR c.hexCode = :hexCode) AND c.isDeleted = false"
			+ ")")
	Boolean existsByNameOrHexCode(String name, String hexCode);

	@Query("SELECT EXISTS ("
			+ "   SELECT 1 FROM ColorEntity c "
			+ "   WHERE (c.name = :name OR c.hexCode = :hexCode) AND c.id <> :id AND c.isDeleted = false"
			+ ")")
	Boolean existsByNameOrHexCodeExceptId(String name, String hexCode, Long id);

	// Fetch 1 include deleted
	@Query("SELECT c FROM ColorEntity c WHERE c.id = :id")
	ColorEntity findIncludingDeletedById(Long id);
}
