package com.todolist.service;

import com.todolist.entity.Task;
import com.todolist.exception.TaskNotFoundException;
import com.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository);
    }

    @Test
    void testCreateTask_Success() {
        Task task = new Task();
        task.setTitle("Nova Tarefa");
        task.setDescription("Descrição");
        task.setStatus("PENDING");

        Task savedTask = new Task(1, "Nova Tarefa", "Descrição", "PENDING", LocalDateTime.now());
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.create(task);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Nova Tarefa", result.getTitle());
        assertEquals("PENDING", result.getStatus());
        assertNotNull(result.getCreatedAt());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTask_SetsCreatedAtWithCurrentTime() {
        Task task = new Task();
        task.setTitle("Tarefa");
        task.setStatus("PENDING");

        LocalDateTime beforeCreate = LocalDateTime.now();
        Task savedTask = new Task(1, "Tarefa", null, "PENDING", LocalDateTime.now());
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.create(task);

        assertNotNull(result.getCreatedAt());
        assertTrue(result.getCreatedAt().isAfter(beforeCreate.minusSeconds(1)));
    }

    @Test
    void testCreateTask_WithoutDescription() {
        Task task = new Task();
        task.setTitle("Tarefa sem descrição");
        task.setStatus("PENDING");

        Task savedTask = new Task(1, "Tarefa sem descrição", null, "PENDING", LocalDateTime.now());
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.create(task);

        assertNotNull(result);
        assertNull(result.getDescription());
    }

    @Test
    void testFindAllTasks_NoFilter() {
        List<Task> tasks = List.of(
                new Task(1, "Tarefa 1", "Desc 1", "PENDING", LocalDateTime.now()),
                new Task(2, "Tarefa 2", "Desc 2", "DONE", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 20), 2);

        when(taskRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Task> result = taskService.findAll(null, PageRequest.of(0, 20));

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(taskRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testFindAllTasks_WithPendingFilter() {
        List<Task> tasks = List.of(
                new Task(1, "Tarefa 1", "Desc 1", "PENDING", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskRepository.findAllByStatus("PENDING", PageRequest.of(0, 20))).thenReturn(page);

        Page<Task> result = taskService.findAll("PENDING", PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        assertEquals("PENDING", result.getContent().get(0).getStatus());
        verify(taskRepository, times(1)).findAllByStatus("PENDING", PageRequest.of(0, 20));
    }

    @Test
    void testFindAllTasks_WithDoneFilter() {
        List<Task> tasks = List.of(
                new Task(2, "Tarefa 2", "Desc 2", "DONE", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskRepository.findAllByStatus("DONE", PageRequest.of(0, 20))).thenReturn(page);

        Page<Task> result = taskService.findAll("DONE", PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        assertEquals("DONE", result.getContent().get(0).getStatus());
    }

    @Test
    void testFindAllTasks_WithInProgressFilter() {
        List<Task> tasks = List.of(
                new Task(3, "Tarefa 3", "Desc 3", "IN_PROGRESS", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskRepository.findAllByStatus("IN_PROGRESS", PageRequest.of(0, 20))).thenReturn(page);

        Page<Task> result = taskService.findAll("IN_PROGRESS", PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        assertEquals("IN_PROGRESS", result.getContent().get(0).getStatus());
    }

    @Test
    void testFindAllTasks_EmptyResult() {
        Page<Task> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Task> result = taskService.findAll(null, PageRequest.of(0, 20));

        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindAllTasks_WithPagination() {
        List<Task> tasks = List.of(
                new Task(21, "Tarefa 21", "Desc", "PENDING", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(1, 20), 21);

        when(taskRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Task> result = taskService.findAll(null, PageRequest.of(1, 20));

        assertEquals(1, result.getNumber());
        assertEquals(21, result.getTotalElements());
    }

    @Test
    void testFindTaskById_Success() {
        Task task = new Task(1, "Tarefa", "Descrição", "PENDING", LocalDateTime.now());
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        Task result = taskService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Tarefa", result.getTitle());
    }

    @Test
    void testFindTaskById_NotFound() {
        when(taskRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.findById(999));
        assertEquals("Tarefa não encontrada com id: 999",
                assertThrows(TaskNotFoundException.class, () -> taskService.findById(999)).getMessage());
    }

    @Test
    void testUpdateTask_Success() {
        Task existing = new Task(1, "Título Antigo", "Desc Antiga", "PENDING", LocalDateTime.now());
        Task update = new Task();
        update.setTitle("Título Novo");
        update.setDescription("Desc Nova");
        update.setStatus("DONE");

        when(taskRepository.findById(1)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(existing);

        Task result = taskService.update(1, update);

        assertEquals("Título Novo", result.getTitle());
        assertEquals("Desc Nova", result.getDescription());
        assertEquals("DONE", result.getStatus());
        verify(taskRepository, times(1)).findById(1);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTask_MaintainsCreatedAt() {
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        Task existing = new Task(1, "Título", "Desc", "PENDING", originalCreatedAt);
        Task update = new Task();
        update.setTitle("Novo Título");
        update.setStatus("DONE");

        when(taskRepository.findById(1)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(existing);

        Task result = taskService.update(1, update);

        assertEquals(originalCreatedAt, result.getCreatedAt());
    }

    @Test
    void testUpdateTask_NotFound() {
        Task update = new Task();
        update.setTitle("Novo");
        update.setStatus("DONE");

        when(taskRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.update(999, update));
    }

    @Test
    void testUpdateTask_ChangesAllFields() {
        Task existing = new Task(1, "T1", "D1", "PENDING", LocalDateTime.now());
        Task update = new Task();
        update.setTitle("T2");
        update.setDescription("D2");
        update.setStatus("IN_PROGRESS");

        when(taskRepository.findById(1)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(existing);

        taskService.update(1, update);

        assertEquals("T2", existing.getTitle());
        assertEquals("D2", existing.getDescription());
        assertEquals("IN_PROGRESS", existing.getStatus());
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> taskService.delete(1));

        verify(taskRepository, times(1)).existsById(1);
        verify(taskRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteTask_NotFound() {
        when(taskRepository.existsById(999)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.delete(999));
        verify(taskRepository, times(1)).existsById(999);
        verify(taskRepository, times(0)).deleteById(999);
    }

    @Test
    void testFindAllTasks_FilterIgnoresBlank() {
        List<Task> tasks = List.of(
                new Task(1, "Tarefa 1", "Desc 1", "PENDING", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskRepository.findAll(any(Pageable.class))).thenReturn(page);

        taskService.findAll("  ", PageRequest.of(0, 20));

        verify(taskRepository, times(1)).findAll(any(Pageable.class));
        verify(taskRepository, times(0)).findAllByStatus(any(), any());
    }

    @Test
    void testCreateTask_DifferentStatuses() {
        String[] statuses = {"PENDING", "IN_PROGRESS", "DONE"};

        for (String status : statuses) {
            Task task = new Task();
            task.setTitle("Tarefa");
            task.setStatus(status);

            Task savedTask = new Task(1, "Tarefa", null, status, LocalDateTime.now());
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            Task result = taskService.create(task);

            assertEquals(status, result.getStatus());
        }
    }
}
