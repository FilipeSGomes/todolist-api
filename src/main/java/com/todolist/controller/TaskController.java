package com.todolist.controller;

import com.todolist.entity.Task;
import com.todolist.request.TaskCreateRequest;
import com.todolist.request.TaskUpdateRequest;
import com.todolist.response.PageResponse;
import com.todolist.response.TaskResponse;
import com.todolist.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Gerenciamento de tarefas")
public class TaskController {

    private final TaskService taskService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Criar tarefa")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tarefa criada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");

        Task created = taskService.create(task);

        TaskResponse response = new TaskResponse(
                created.getId(),
                created.getTitle(),
                created.getDescription(),
                created.getStatus(),
                created.getCreatedAt().format(DATE_TIME_FORMATTER)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar tarefas", description = "Retorna lista paginada, com filtro opcional por status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping
    public ResponseEntity<PageResponse<TaskResponse>> findAll(
            @Parameter(description = "Filtrar por status: PENDING, IN_PROGRESS, COMPLETED")
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskService.findAll(status, pageable);

        PageResponse<TaskResponse> response = new PageResponse<>();
        response.setContent(taskPage.getContent().stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getCreatedAt().format(DATE_TIME_FORMATTER)
                ))
                .toList());
        response.setTotalElements(taskPage.getTotalElements());
        response.setTotalPages(taskPage.getTotalPages());
        response.setNumber(taskPage.getNumber());
        response.setSize(taskPage.getSize());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar tarefa por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Integer id) {
        Task task = taskService.findById(id);

        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt().format(DATE_TIME_FORMATTER)
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar tarefa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tarefa atualizada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody TaskUpdateRequest request) {

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());

        Task updated = taskService.update(id, task);

        TaskResponse response = new TaskResponse(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getStatus(),
                updated.getCreatedAt().format(DATE_TIME_FORMATTER)
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deletar tarefa")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tarefa deletada"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
