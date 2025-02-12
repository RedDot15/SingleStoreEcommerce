package com.example.project_economic.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class OrderEntity {
	@Id
	String id;

	BigDecimal totalAmount;

	LocalDateTime boughtAt;

	Boolean received;

	String status;

	@ManyToOne
	@JoinColumn(name = "user_id")
	UserEntity userEntity;

	@OneToMany
	@JoinColumn(name = "order_id")
	List<OrderItemEntity> orderItemEntityList;

	@PrePersist
	void control() {
		setBoughtAt(LocalDateTime.now());
		setReceived(false);
		setStatus("Pending");
	}
}
