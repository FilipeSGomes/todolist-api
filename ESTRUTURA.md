# Estrutura do Projeto

Nova estrutura simplificada e intuitiva.

## Organização de Pastas

```
src/main/java/com/todolist/
├── TodolistApiApplication.java
├── entity/                    # Entidades JPA (banco de dados)
│   ├── Task.java
│   └── User.java
├── repository/                # Acesso ao banco (Spring Data)
│   ├── TaskRepository.java
│   └── UserRepository.java
├── service/                   # Lógica de negócio
│   └── TaskService.java
├── controller/                # Endpoints REST
│   └── TaskController.java
├── request/                   # DTOs de entrada
│   ├── TaskCreateRequest.java
│   └── TaskUpdateRequest.java
├── response/                  # DTOs de saída
│   ├── TaskResponse.java
│   └── PageResponse.java
├── exception/                 # Exceções personalizadas
│   └── TaskNotFoundException.java
└── config/                    # Configurações da aplicação
    ├── SecurityConfig.java
    ├── OpenApiConfig.java
    ├── GlobalExceptionHandler.java
    └── security/
        └── CustomUserDetailsService.java
```

## Explicação de Cada Camada

### entity/
Entidades JPA que representam as tabelas do banco de dados.
- Mapeadas com anotações como @Entity, @Table, @Column
- Correspondem diretamente às migrations Flyway

### repository/
Interfaces Spring Data JPA para acesso ao banco.
- Herdam de JpaRepository
- Definem métodos customizados de busca
- Nunca contêm lógica de negócio

### service/
Contém toda a lógica de negócio da aplicação.
- Orquestra repositories
- Valida regras de negócio
- Lança exceções customizadas

### controller/
Endpoints REST da aplicação.
- Recebe requests, chama services, retorna responses
- Valida entrada com @Valid
- Mapeia Domain → Response

### request/
DTOs para dados que vêm do cliente.
- TaskCreateRequest: para POST
- TaskUpdateRequest: para PUT
- Contêm validações com Bean Validation

### response/
DTOs para dados que vão para o cliente.
- TaskResponse: resposta de uma tarefa
- PageResponse: resposta paginada
- Nunca contêm informações sensíveis (senhas)

### exception/
Exceções customizadas da aplicação.
- TaskNotFoundException: tarefa não encontrada
- Capturadas pelo GlobalExceptionHandler

### config/
Configurações da aplicação.
- SecurityConfig: autenticação e autorização, libera endpoints públicos (Swagger, health)
- OpenApiConfig: bean que registra a spec OpenAPI 3 com autenticação Basic Auth
- GlobalExceptionHandler: tratamento de erros
- CustomUserDetailsService: carregamento de usuários

## Fluxo de uma Requisição

1. **Controller** recebe a requisição (HTTP POST /api/v1/tasks)
2. **Controller** valida com @Valid
3. **Controller** mapeia Request → Entity
4. **Service** implementa lógica de negócio
5. **Repository** persiste no banco
6. **Service** retorna Entity
7. **Controller** mapeia Entity → Response
8. **Controller** retorna Response (HTTP 201)

## Exemplo: Criar Tarefa

```
POST /api/v1/tasks
{
  "title": "Minha tarefa"
}

↓

TaskController.create(TaskCreateRequest)
  ↓
  TaskService.create(Task)
    ↓
    TaskRepository.save(Task)  ← Persiste no banco
    ↓
  Retorna Task com ID
  ↓
Mapeia para TaskResponse
  ↓
HTTP 201 + JSON Response
```

## Vantagens desta Estrutura

- ✅ **Simples**: fácil de entender em primeira vista
- ✅ **Escalável**: adicionar features não desorganiza o código
- ✅ **Testável**: cada camada tem responsabilidade clara
- ✅ **Padrão**: segue convenções do Spring Boot
- ✅ **Intuitivo**: naming deixa claro o que cada classe faz
