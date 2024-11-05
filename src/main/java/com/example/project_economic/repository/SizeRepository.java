package com.example.project_economic.repository;

import com.example.project_economic.entity.SizeEntity;
import org.hibernate.engine.jdbc.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface SizeRepository extends JpaRepository<SizeEntity, Long> {
    Set<SizeEntity> findAllByName(String sizeName);

    Set<SizeEntity> findAllByNameAndIdIsNot(String sizeName, Long sizeId);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM SizeEntity s" +
            "   WHERE s.id = :id AND s.isDeleted = false" +
            ")")
    boolean existsById(Long id);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM SizeEntity s" +
            "   WHERE s.name = :name AND s.isDeleted = false" +
            ")")
    Boolean existsByName(String name);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM SizeEntity s" +
            "   WHERE s.name = :name AND s.id <> :id AND s.isDeleted = false" +
            ")")
    Boolean existsByNameExceptId(String name, Long id);

    SizeEntity findFirstById(Long id);
}
