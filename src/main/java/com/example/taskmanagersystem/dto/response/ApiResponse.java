package com.example.taskmanagersystem.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

//lombok
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
//show only not null json object
@JsonInclude(JsonInclude.Include.NON_NULL)
//standardization response
public class ApiResponse<T> {
    @Builder.Default
    int code = 1000;
    String message;
    T result;

}
