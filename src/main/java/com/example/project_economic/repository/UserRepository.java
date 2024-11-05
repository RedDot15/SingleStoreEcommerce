package com.example.project_economic.repository;

import com.example.project_economic.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM UserEntity u" +
            "   WHERE u.id = :id  AND u.isDeleted = false" +
            ")")
    boolean existsById(Long id);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM UserEntity u" +
            "   WHERE (u.username = :username OR u.email = :email) AND u.isDeleted = false" +
            ")")
    Boolean existsByUsernameOrEmail(String username, String email);

    @Query("SELECT EXISTS (" +
            "   SELECT 1 FROM UserEntity u" +
            "   WHERE (u.username = :username OR u.email = :email) AND u.id <> :id AND u.isDeleted = false" +
            ")")
    Boolean existsByUsernameOrEmailExceptId(String username, String email, Long id);

    UserEntity findFirstById(Long id);
}
