package com.example.project_economic.entity;

import jakarta.persistence.*;
import java.util.Set;
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
@SQLDelete(sql = "UPDATE color SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Entity
@Table(name = "color")
public class ColorEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String name;

	String hexCode;

	Boolean isDeleted;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "color_id")
	Set<ProductDetailEntity> productDetailEntitySet;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "color_id")
	Set<ProductImageEntity> productImageEntitySet;

	@PrePersist
	void control() {
		setIsDeleted(false);
	}
}
