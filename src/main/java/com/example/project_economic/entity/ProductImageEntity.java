package com.example.project_economic.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE product_image SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Entity
@Table(name = "product_image")
public class ProductImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    ProductEntity productEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "color_id")
    ColorEntity colorEntity;

    Boolean isActive;

    Boolean isDeleted;

    @PrePersist
    void control(){
        setIsActive(true);
        setIsDeleted(false);
    }
}
