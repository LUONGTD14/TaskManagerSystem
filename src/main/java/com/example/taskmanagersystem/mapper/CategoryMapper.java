package com.example.taskmanagersystem.mapper;

import com.example.taskmanagersystem.dto.request.CategoryRequest;
import com.example.taskmanagersystem.dto.response.CategoryResponse;
import com.example.taskmanagersystem.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest dto);

    CategoryResponse toDto(Category category);
}