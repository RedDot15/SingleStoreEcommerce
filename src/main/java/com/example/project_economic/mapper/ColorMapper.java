package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.ColorRequest;
import com.example.project_economic.dto.response.ColorResponse;
import com.example.project_economic.entity.ColorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ColorMapper {
    ColorResponse toColorResponse(ColorEntity colorEntity);
    ColorEntity toColorEntity(ColorRequest colorRequest);
    @Mapping(source = "colorId", target = "id")
    ColorEntity toColorEntity(Long colorId);
}
