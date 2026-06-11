package com.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.todolist.config.EarlyDatabaseInitializer;

@SpringBootApplication
public class TodolistApiApplication {

    public static void main(String[] args) {
        EarlyDatabaseInitializer.ensureDatabase();
        SpringApplication.run(TodolistApiApplication.class, args);
    }
}
