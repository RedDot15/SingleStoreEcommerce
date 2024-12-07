package com.example.project_economic.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Entity
@Table(name = "product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String description;

    BigDecimal costPrice;

    BigDecimal salePrice;

    Integer likes;

    Boolean isActive;

    Boolean isDeleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",referencedColumnName = "id")
    CategoryEntity categoryEntity;

    @Where(clause = "is_active = true")
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Set<ProductImageEntity> activeProductImageEntitySet;

    @Where(clause = "is_active = true")
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Set<ProductDetailEntity> activeProductDetailEntitySet;

    @Column(name = "category_id", insertable = false, updatable = false)
    Long categoryId;

    @PrePersist
    void control(){
        setLikes(0);
        setIsActive(false);
        setIsDeleted(false);
    }
}
