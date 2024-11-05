package com.example.project_economic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE product_detail SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Entity
@Table(name = "product_detail")
public class ProductDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    ProductEntity productEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "color_id")
    ColorEntity colorEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "size_id")
    SizeEntity sizeEntity;

    Integer stock;
    Boolean isActive;
    Boolean isDeleted;

    @PrePersist
    void control(){
        setIsActive(true);
        setIsDeleted(false);
    }
}
