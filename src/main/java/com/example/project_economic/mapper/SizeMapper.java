package com.example.project_economic.mapper;

import com.example.project_economic.dto.request.SizeRequest;
import com.example.project_economic.dto.response.SizeResponse;
import com.example.project_economic.entity.SizeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SizeMapper {
	// Add
	SizeEntity toSizeEntity(SizeRequest sizeRequest);

	// Update
	void updateSizeEntityFromRequest(@MappingTarget SizeEntity sizeEntity, SizeRequest sizeRequest);

	// Response
	SizeResponse toSizeResponse(SizeEntity sizeEntity);
}
