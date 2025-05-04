package com.example.taskmanagersystem.mapper;

import com.example.taskmanagersystem.dto.request.TaskRequest;
import com.example.taskmanagersystem.dto.response.TaskResponse;
import com.example.taskmanagersystem.model.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task toEntity(TaskRequest dto);

    TaskResponse toDto(Task task);
}
