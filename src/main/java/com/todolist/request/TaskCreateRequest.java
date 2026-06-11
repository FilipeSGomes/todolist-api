package com.todolist.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de tarefa")
public class TaskCreateRequest {

    @Schema(description = "Título da tarefa", example = "Estudar Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Título é obrigatório")
    @Size(min = 1, max = 255, message = "Título deve ter entre 1 e 255 caracteres")
    private String title;

    @Schema(description = "Descrição detalhada", example = "Estudar Spring Security e JPA")
    @Size(max = 1000, message = "Descrição não pode ter mais de 1000 caracteres")
    private String description;

    @Schema(description = "Status inicial (padrão: PENDING)", example = "PENDING", allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"})
    private String status;

    public TaskCreateRequest() {
    }

    public TaskCreateRequest(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
