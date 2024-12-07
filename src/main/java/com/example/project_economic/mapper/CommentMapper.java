package com.example.project_economic.mapper;

import com.example.project_economic.dto.response.CommentResponse;
import com.example.project_economic.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    // Response
    @Mapping(target = "userResponse", source = "userEntity")
    CommentResponse toCommentResponse(CommentEntity commentEntity);
}
