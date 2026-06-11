package com.todolist.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização de tarefa")
public class TaskUpdateRequest {

    @Schema(description = "Título da tarefa", example = "Estudar Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Título é obrigatório")
    @Size(min = 1, max = 255, message = "Título deve ter entre 1 e 255 caracteres")
    private String title;

    @Schema(description = "Descrição detalhada", example = "Estudar Spring Security e JPA")
    @Size(max = 1000, message = "Descrição não pode ter mais de 1000 caracteres")
    private String description;

    @Schema(description = "Status da tarefa", example = "IN_PROGRESS", allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Status é obrigatório")
    private String status;

    public TaskUpdateRequest() {
    }

    public TaskUpdateRequest(String title, String description, String status) {
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
