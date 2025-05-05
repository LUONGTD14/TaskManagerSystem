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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
        List<TaskResponse> allTasks = taskService.getAllTasks();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        ZonedDateTime nowVN = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        List<TaskResponse> expiredTasks = allTasks.stream()
                .filter(task -> {
                    try {
                        ZonedDateTime end = ZonedDateTime.parse(task.getEndTime(), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));
                        return nowVN.isBefore(end) && !task.getStatus().equals("done");
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(expiredTasks);
    }
    @GetMapping("/over")
    public ResponseEntity<List<TaskResponse>> getAllTasksOver() {
        List<TaskResponse> allTasksOver = taskService.getAllTasks();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        ZonedDateTime nowVN = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        List<TaskResponse> expiredTasks = allTasksOver.stream()
                .filter(task -> {
                    try {
                        ZonedDateTime end = ZonedDateTime.parse(task.getEndTime(), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));
                        return end.isBefore(nowVN) || task.getStatus().equals("done");
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(expiredTasks);
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
