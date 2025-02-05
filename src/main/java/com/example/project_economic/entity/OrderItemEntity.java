package com.example.project_economic.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "order_item")
public class OrderItemEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne
  @JoinColumn(name = "order_id")
  OrderEntity orderEntity;

  @ManyToOne
  @JoinColumn(name = "product_detail_id")
  ProductDetailEntity productDetailEntity;

  @Column(name = "product_detail_id", insertable = false, updatable = false)
  Long productDetailId;

  Integer quantity;
}
