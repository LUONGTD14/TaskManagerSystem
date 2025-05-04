package com.example.taskmanagersystem.service;

import com.example.taskmanagersystem.dto.request.TaskRequest;
import com.example.taskmanagersystem.dto.response.TaskResponse;
import com.example.taskmanagersystem.mapper.TaskMapper;
import com.example.taskmanagersystem.model.Task;
import com.google.firebase.database.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class TaskService {
    @Autowired
    private TaskMapper taskMapper;

    private DatabaseReference taskRef;

    @PostConstruct
    public void init() {
        this.taskRef = FirebaseDatabase.getInstance().getReference("tasks");
    }

    public TaskResponse addTask(TaskRequest request) {
        Task task = taskMapper.toEntity(request);
        task.setId(UUID.randomUUID().toString());
        task.setStatus("pending");


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

        task.setCreatedAt(formatter.format(Instant.now()));
        if (request.getStartTime() != null) {
            task.setStartTime(formatter.format(Instant.parse(request.getStartTime())));
        }
        if (request.getEndTime() != null) {
            task.setEndTime(formatter.format(Instant.parse(request.getEndTime())));
        }
        if (request.getReminderTime() != null) {
            task.setReminderTime(formatter.format(Instant.parse(request.getReminderTime())));
        }

        taskRef.child(task.getId()).setValueAsync(task);
        return taskMapper.toDto(task);
    }

    public List<TaskResponse> getAllTasks() {
        List<TaskResponse> list = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Task task = child.getValue(Task.class);
                    if (task != null) {
                        list.add(taskMapper.toDto(task));
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return list;
    }

    public boolean markComplete(String taskId) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setStatus("done");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
            ZonedDateTime nowVN = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            String formatted = nowVN.format(formatter);
            task.setDoneAt(formatted);
            taskRef.child(taskId).setValueAsync(task);
            return true;
        }
        return false;
    }

    public boolean editTaskDescription(String taskId, String newDescription) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setDescription(newDescription);
            taskRef.child(taskId).setValueAsync(task);
            return true;
        }
        return false;
    }

    public boolean extendTaskTime(String taskId, String reason, String endTime) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setExtensionReason(task.getExtensionReason() + "\n" + task.getEndTime() + "\n" + reason);
            task.setEndTime(endTime);
            taskRef.child(taskId).setValueAsync(task);
            return true;
        }
        return false;
    }

    private Task getTaskById(String taskId) {
        CountDownLatch latch = new CountDownLatch(1);
        final Task[] task = new Task[1];

        taskRef.child(taskId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                task[0] = snapshot.getValue(Task.class);
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return task[0];
    }

}