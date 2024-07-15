package com.example.project_economic.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name",unique = true)
    String name;
    String createdDate;
    Boolean isActive;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    List<ProductEntity> productEntityList;

    public CategoryEntity(String name){
        this.name = name;
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        this.createdDate=dateTimeFormatter.format(LocalDateTime.now());
        this.isActive = true;
    }

    public void updateTime(){
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        this.createdDate=dateTimeFormatter.format(LocalDateTime.now());
    }
}
