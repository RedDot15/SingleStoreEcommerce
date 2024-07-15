package com.example.project_economic.repository;

import com.example.project_economic.entity.SizeEntity;
import org.hibernate.engine.jdbc.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SizeRepository extends JpaRepository<SizeEntity, Long> {
}
