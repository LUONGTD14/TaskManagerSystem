package com.example.taskmanagersystem.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task {
    String id;
    String title;
    String description;
    String startTime;
    String endTime;
    String reminderTime;
    String categoryId;
    String managerId;
    String memberId1;
    String memberId2;
    String status; // mặc định = "pending"
    String createdAt;
    String doneAt;
    String extensionReason;
}

