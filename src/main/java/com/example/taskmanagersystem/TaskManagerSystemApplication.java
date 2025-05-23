package com.example.taskmanagersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskManagerSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerSystemApplication.class, args);
    }

}
