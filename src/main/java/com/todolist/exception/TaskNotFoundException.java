package com.todolist.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Integer id) {
        super("Tarefa não encontrada com id: " + id);
    }
}
