package com.example.project_economic.repository;

import com.example.project_economic.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUsername(String username);

    // Exist
    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM UserEntity u" +
            "   WHERE (u.username = :username OR u.email = :email) AND u.isDeleted = false" +
            ")")
    Boolean existsByUsernameOrEmail(String username, String email);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM UserEntity u" +
            "   WHERE u.email = :email AND u.id <> :id AND u.isDeleted = false" +
            ")")
    Boolean existsByEmailExceptId(String email, Long id);

}
