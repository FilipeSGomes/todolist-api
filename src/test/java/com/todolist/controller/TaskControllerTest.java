package com.todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.entity.Task;
import com.todolist.exception.TaskNotFoundException;
import com.todolist.request.TaskCreateRequest;
import com.todolist.request.TaskUpdateRequest;
import com.todolist.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:test",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateTask_Success() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest("Nova Tarefa", "Descrição", "PENDING");
        Task createdTask = new Task(1, "Nova Tarefa", "Descrição", "PENDING", LocalDateTime.now());

        when(taskService.create(any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Nova Tarefa"))
                .andExpect(jsonPath("$.description").value("Descrição"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateTask_WithoutDescription() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest("Tarefa", null, null);
        Task createdTask = new Task(1, "Tarefa", null, "PENDING", LocalDateTime.now());

        when(taskService.create(any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateTask_Unauthorized() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest("Tarefa", "Desc", "PENDING");

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateTask_BlankTitle() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest("", "Descrição", "PENDING");

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].field", hasItem("title")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateTask_TitleTooLong() throws Exception {
        String longTitle = "a".repeat(256);
        TaskCreateRequest request = new TaskCreateRequest(longTitle, "Desc", "PENDING");

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field", hasItem("title")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateTask_DescriptionTooLong() throws Exception {
        String longDesc = "a".repeat(1001);
        TaskCreateRequest request = new TaskCreateRequest("Tarefa", longDesc, "PENDING");

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field", hasItem("description")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllTasks() throws Exception {
        List<Task> tasks = List.of(
                new Task(1, "Tarefa 1", "Desc 1", "PENDING", LocalDateTime.now()),
                new Task(2, "Tarefa 2", "Desc 2", "DONE", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 20), 2);

        when(taskService.findAll(null, PageRequest.of(0, 20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllTasks_WithPendingFilter() throws Exception {
        List<Task> tasks = List.of(
                new Task(1, "Tarefa 1", "Desc", "PENDING", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskService.findAll("PENDING", PageRequest.of(0, 20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks?status=PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllTasks_WithDoneFilter() throws Exception {
        List<Task> tasks = List.of(
                new Task(2, "Tarefa 2", "Desc", "DONE", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskService.findAll("DONE", PageRequest.of(0, 20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks?status=DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("DONE"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllTasks_WithInProgressFilter() throws Exception {
        List<Task> tasks = List.of(
                new Task(3, "Tarefa 3", "Desc", "IN_PROGRESS", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(taskService.findAll("IN_PROGRESS", PageRequest.of(0, 20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks?status=IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllTasks_WithPagination() throws Exception {
        List<Task> tasks = List.of(
                new Task(21, "Tarefa 21", "Desc", "PENDING", LocalDateTime.now())
        );
        Page<Task> page = new PageImpl<>(tasks, PageRequest.of(1, 20), 21);

        when(taskService.findAll(null, PageRequest.of(1, 20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks?page=1&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(21));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllTasks_Empty() throws Exception {
        Page<Task> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

        when(taskService.findAll(null, PageRequest.of(0, 20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetTaskById_Success() throws Exception {
        Task task = new Task(1, "Tarefa 1", "Descrição", "PENDING", LocalDateTime.now());

        when(taskService.findById(1)).thenReturn(task);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Tarefa 1"))
                .andExpect(jsonPath("$.description").value("Descrição"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetTaskById_NotFound() throws Exception {
        when(taskService.findById(999)).thenThrow(new TaskNotFoundException(999));

        mockMvc.perform(get("/api/v1/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Tarefa não encontrada com id: 999"));
    }

    @Test
    void testGetTaskById_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateTask_Success() throws Exception {
        TaskUpdateRequest request = new TaskUpdateRequest("Tarefa Atualizada", "Desc Atualizada", "DONE");
        Task updatedTask = new Task(1, "Tarefa Atualizada", "Desc Atualizada", "DONE", LocalDateTime.now());

        when(taskService.update(eq(1), any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tarefa Atualizada"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateTask_BlankTitle() throws Exception {
        TaskUpdateRequest request = new TaskUpdateRequest("", "Desc", "DONE");

        mockMvc.perform(put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field", hasItem("title")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateTask_MissingStatus() throws Exception {
        String json = "{\"title\":\"Tarefa\",\"description\":\"Desc\"}";

        mockMvc.perform(put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field", hasItem("status")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateTask_NotFound() throws Exception {
        TaskUpdateRequest request = new TaskUpdateRequest("Tarefa", "Desc", "DONE");

        when(taskService.update(eq(999), any(Task.class))).thenThrow(new TaskNotFoundException(999));

        mockMvc.perform(put("/api/v1/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDeleteTask_Success() throws Exception {
        doNothing().when(taskService).delete(1);

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).delete(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDeleteTask_NotFound() throws Exception {
        doThrow(new TaskNotFoundException(999)).when(taskService).delete(999);

        mockMvc.perform(delete("/api/v1/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTask_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testHealthEndpoint_NoAuth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testCreateTask_WithUserRole() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest("Tarefa", "Desc", "PENDING");
        Task createdTask = new Task(1, "Tarefa", "Desc", "PENDING", LocalDateTime.now());

        when(taskService.create(any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetTaskById_VerifyDateFormat() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(1, "Tarefa", "Desc", "PENDING", now);

        when(taskService.findById(1)).thenReturn(task);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.createdAt").value(matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateTask_AllStatuses() throws Exception {
        String[] statuses = {"PENDING", "IN_PROGRESS", "DONE"};

        for (String status : statuses) {
            TaskUpdateRequest request = new TaskUpdateRequest("Tarefa", "Desc", status);
            Task updatedTask = new Task(1, "Tarefa", "Desc", status, LocalDateTime.now());

            when(taskService.update(eq(1), any(Task.class))).thenReturn(updatedTask);

            mockMvc.perform(put("/api/v1/tasks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(status));
        }
    }
}
