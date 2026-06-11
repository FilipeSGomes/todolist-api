# TodoList API

API RESTful para gerenciamento de tarefas, desenvolvida com Spring Boot 3 e Java 21.

## Tecnologias

- Java 21
- Spring Boot 3.3
- Spring Security (Basic Auth)
- Spring Data JPA
- SQL Server (via Docker)
- Flyway (migrations)
- Springdoc OpenAPI 2 (Swagger UI)
- JUnit 5 + Mockito
- JaCoCo (cobertura de testes)
- GitHub Actions (CI/CD)

## Funcionalidades

- CRUD completo de tarefas
- Filtro de tarefas por status com paginação
- Autenticação HTTP Basic com dois perfis de acesso (ADMIN, USER)
- Documentação interativa via Swagger UI
- Migrations versionadas com Flyway
- Health check via Spring Actuator

## Pré-requisitos

- Java 21
- Maven
- Docker e Docker Compose

## Setup rápido

```bash
./setup.sh
```

O script sobe o SQL Server via Docker, cria o banco, executa as migrations e compila a aplicação.

## Iniciar a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## Usuários padrão

| Username | Senha    | Role       |
|----------|----------|------------|
| admin    | admin123 | ROLE_ADMIN |
| user     | user123  | ROLE_USER  |

## Documentação (Swagger UI)

Após subir a aplicação, acesse:

```
http://localhost:8080/swagger-ui.html
```

Clique em **Authorize** e informe as credenciais para testar os endpoints autenticados.

O JSON da spec OpenAPI está disponível em:

```
http://localhost:8080/v3/api-docs
```

## Endpoints

| Método | Endpoint            | Descrição                              |
|--------|---------------------|----------------------------------------|
| POST   | /api/v1/tasks       | Criar tarefa                           |
| GET    | /api/v1/tasks       | Listar tarefas (paginado, filtro status)|
| GET    | /api/v1/tasks/{id}  | Buscar tarefa por ID                   |
| PUT    | /api/v1/tasks/{id}  | Atualizar tarefa                       |
| DELETE | /api/v1/tasks/{id}  | Deletar tarefa                         |
| GET    | /actuator/health    | Health check (público)                 |

## Status das tarefas

| Valor       | Descrição    |
|-------------|--------------|
| PENDING     | Pendente     |
| IN_PROGRESS | Em andamento |
| COMPLETED   | Concluída    |

## Exemplos de uso

**Criar tarefa:**
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Estudar Spring Boot", "description": "Foco em Security e JPA"}'
```

**Listar tarefas com filtro:**
```bash
curl -u admin:admin123 "http://localhost:8080/api/v1/tasks?status=PENDING&page=0&size=10"
```

**Buscar por ID:**
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/tasks/1
```

**Atualizar tarefa:**
```bash
curl -u admin:admin123 -X PUT http://localhost:8080/api/v1/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Estudar Spring Boot", "status": "COMPLETED"}'
```

**Deletar tarefa:**
```bash
curl -u admin:admin123 -X DELETE http://localhost:8080/api/v1/tasks/1
```

**Health check:**
```bash
curl http://localhost:8080/actuator/health
```

## Testes

```bash
mvn test
```

Cobertura de testes com JaCoCo:
```bash
mvn test jacoco:report
# Relatório em: target/site/jacoco/index.html
```

## CI/CD

O pipeline GitHub Actions (`.github/workflows/ci-cd.yml`) executa automaticamente:

1. **Build** — compila e empacota o JAR
2. **Test** — roda os testes e gera relatório de cobertura (Codecov)
3. **Deploy** — deploy no Azure App Service (apenas branch `main`)

## Configuração do banco (manual)

Variáveis de ambiente suportadas:

| Variável     | Padrão         |
|--------------|----------------|
| DB_HOST      | localhost      |
| DB_PORT      | 1433           |
| DB_NAME      | todolist       |
| DB_USER      | sa             |
| DB_PASSWORD  | YourPassword123! |

## Uso de Inteligência Artificial

Este projeto contou com o auxílio do **Claude (Anthropic)** via Claude Code nas seguintes frentes:

- **Diagnóstico e correção de bugs**: identificação de hashes BCrypt inválidos nos scripts de seed que impediam autenticação
- **Atualização de CI/CD**: detecção e correção de actions deprecated (`upload-artifact@v3` → `v4`) no pipeline GitHub Actions
- **Implementação do Swagger**: adição da dependência `springdoc-openapi-starter-webmvc-ui`, criação do bean `OpenApiConfig`, liberação dos endpoints no `SecurityConfig` e anotações OpenAPI no controller e DTOs
- **Revisão de qualidade**: validação de boas práticas (Clean Code, separação de camadas, convenções de commit)

Todos os prompts foram direcionados a tarefas específicas de diagnóstico, correção e incremento — o código de negócio e a arquitetura foram definidos pelo desenvolvedor.
