package com.example.taskmanagersystem.controller;

import com.example.taskmanagersystem.dto.response.TaskResponse;
import com.example.taskmanagersystem.model.NotificationMessage;
import com.example.taskmanagersystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private TaskService taskService;

    @PostMapping("/api/notifyTaskDone")
    public void notifyTaskCompleted(@RequestBody NotificationMessage message) {
        System.out.println("Sent notification to topic");
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }

    @Scheduled(fixedRate = 5*60*60000)
    public void checkReminders() {
        System.out.println("Checking reminderTime...");
        List<TaskResponse> tasks = taskService.getAllTasks();
        for (TaskResponse task : tasks) {
            if (task.getReminderTime() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
                ZonedDateTime remined = ZonedDateTime.parse(task.getReminderTime(), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));

                ZonedDateTime nowVN = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

                System.out.println("Reminder Time: " + remined + " | Now: " + nowVN);
                if (remined.isBefore(nowVN) && !task.getStatus().equals("done")) {
                    System.out.println("send..." + task.getId());
                    NotificationMessage message = new NotificationMessage(
                            "Have a task need complete",
                            "Task " + task.getId() + " !!!!!!!!!!!!"
                    );

                    messagingTemplate.convertAndSend("/topic/notifications", message);
                }
            }
        }
    }
}
