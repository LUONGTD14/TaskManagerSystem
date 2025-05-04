package com.example.taskmanagersystem.mapper;

import com.example.taskmanagersystem.dto.request.MemberRequest;
import com.example.taskmanagersystem.dto.response.MemberResponse;
import com.example.taskmanagersystem.model.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member toEntity(MemberRequest dto);

    MemberResponse toDto(Member member);
}