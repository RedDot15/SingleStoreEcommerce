package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.SizeResponse;
import com.example.project_economic.entity.SizeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SizeMapper {
    SizeResponse toSizeResponse(SizeEntity sizeEntity);
    SizeEntity toSizeEntity(SizeRequest sizeRequest);
    @Mapping(source = "sizeId", target = "id")
    SizeEntity toSizeEntity(Long sizeId);
}
