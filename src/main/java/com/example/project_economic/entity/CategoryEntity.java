package com.example.project_economic.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE category SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Entity
@Table(name = "category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name",unique = true)
    String name;

    LocalDateTime createdDate;

    Boolean isActive;

    Boolean isDeleted;

    @Where(clause = "is_active = true")
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Set<ProductEntity> activeProductEntitySet;

    @PrePersist
    public void control() {
        setCreatedDate(LocalDateTime.now());
        setIsActive(false);
        setIsDeleted(false);
    }
}

