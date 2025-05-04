package com.example.taskmanagersystem.service;
import com.example.taskmanagersystem.dto.request.TaskRequest;
import com.example.taskmanagersystem.dto.response.TaskResponse;
import com.example.taskmanagersystem.mapper.TaskMapper;
import com.example.taskmanagersystem.model.Task;
import com.google.firebase.database.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;

    private DatabaseReference taskRef;

    @PostConstruct
    public void init() {
        this.taskRef = FirebaseDatabase.getInstance().getReference("tasks");
    }

    public TaskResponse addTask(TaskRequest request) {
        Task task = taskMapper.toEntity(request);
        task.setId(UUID.randomUUID().toString());
        task.setStatus("pending");
        task.setCreatedAt(Instant.now().toString());

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

    // Đánh dấu nhiệm vụ là hoàn thành
    public boolean markComplete(String taskId) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setStatus("done");
            task.setDoneAt(Instant.now().toString());
            taskRef.child(taskId).setValueAsync(task);
            return true;
        }
        return false;
    }

    // Chỉnh sửa mô tả nhiệm vụ
    public boolean editTaskDescription(String taskId, String newDescription) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setDescription(newDescription);
            taskRef.child(taskId).setValueAsync(task);
            return true;
        }
        return false;
    }

    // Gia hạn thời gian và lưu lý do
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

    // Lấy nhiệm vụ theo ID
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