package com.example.taskmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberRequest {
    String name;
    String knoxId;
    String phone;
}

