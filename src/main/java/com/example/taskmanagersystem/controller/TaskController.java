package com.example.taskmanagersystem.controller;

import com.example.taskmanagersystem.dto.request.TaskExtensionRequest;
import com.example.taskmanagersystem.dto.request.TaskRequest;
import com.example.taskmanagersystem.dto.request.TaskUpdateRequest;
import com.example.taskmanagersystem.dto.response.TaskResponse;
import com.example.taskmanagersystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> addTask(@RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.addTask(request));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<Void> markComplete(@PathVariable String taskId) {
        boolean updated = taskService.markComplete(taskId);
        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{taskId}/edit")
    public ResponseEntity<Void> editTask(@PathVariable String taskId, @RequestBody TaskUpdateRequest updateRequest) {
        boolean updated = taskService.editTaskDescription(taskId, updateRequest.getDescription());
        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{taskId}/extend")
    public ResponseEntity<Void> extendTime(@PathVariable String taskId, @RequestBody TaskExtensionRequest extensionRequest) {
        boolean updated = taskService.extendTaskTime(taskId, extensionRequest.getReason(), extensionRequest.getEndTime());
        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
