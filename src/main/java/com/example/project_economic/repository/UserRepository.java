package com.example.project_economic.repository;

import com.example.project_economic.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  @Query(
      "SELECT u FROM UserEntity u "
          + "WHERE u.username = :username AND u.isActive = true AND u.isDeleted = false")
  Optional<UserEntity> findActiveByUsername(String username);

  // Exist
  @Query(
      "SELECT EXISTS ("
          + "   SELECT 1 FROM UserEntity u"
          + "   WHERE u.username = :username AND u.isDeleted = false"
          + ")")
  Boolean existsByUsername(String username);

  @Query(
      "SELECT EXISTS ("
          + "   SELECT 1 FROM UserEntity u"
          + "   WHERE u.email = :email AND u.isDeleted = false"
          + ")")
  Boolean existsByEmail(String email);

  @Query(
      "SELECT EXISTS ("
          + "   SELECT 1 FROM UserEntity u"
          + "   WHERE u.email = :email AND u.id <> :id AND u.isDeleted = false"
          + ")")
  Boolean existsByEmailExceptId(String email, Long id);
}
