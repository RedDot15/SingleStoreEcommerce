package com.example.project_economic.mapper.comment;

import com.example.project_economic.dto.response.comment.CommentResponse;
import com.example.project_economic.entity.comment.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
	// Response
	@Mapping(target = "userResponse", source = "userEntity")
	CommentResponse toCommentResponse(CommentEntity commentEntity);
}
