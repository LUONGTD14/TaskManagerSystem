package com.example.taskmanagersystem.controller;

import com.example.taskmanagersystem.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/api/notifyTaskDone")
    public void notifyTaskCompleted(@RequestBody NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }
}
