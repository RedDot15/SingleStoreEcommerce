package com.example.project_economic.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "order")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    LocalDateTime boughtAt;

    Boolean received;

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity userEntity;

    @PrePersist
    void control(){
        setBoughtAt(LocalDateTime.now());
        setReceived(false);
    }
}
