package com.example.project_economic.entity.comment;

import com.example.project_economic.entity.ProductEntity;
import com.example.project_economic.entity.UserEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comment")
public class CommentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String content;

	Integer star;

	Integer likeCount;

	Integer dislike;

	LocalDateTime createdAt;

	LocalDateTime updatedAt;

	@ManyToOne
	@JoinColumn(name = "user_id")
	UserEntity userEntity;

	@ManyToOne
	@JoinColumn(name = "product_id")
	ProductEntity productEntity;

	@PrePersist
	void control() {
		setLikeCount(0);
		setDislike(0);
		setCreatedAt(LocalDateTime.now());
		setUpdatedAt(LocalDateTime.now());
	}
}
