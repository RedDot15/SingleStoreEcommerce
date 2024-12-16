package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ColorResponse;
import com.example.project_economic.entity.ColorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ColorMapper {
    // Add
    ColorEntity toColorEntity(ColorRequest colorRequest);

    // Update
    void updateColorEntityFromRequest(@MappingTarget ColorEntity colorEntity, ColorRequest colorRequest);

    // Response
    ColorResponse toColorResponse(ColorEntity colorEntity);
}
