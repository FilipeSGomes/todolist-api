# TodoList API

API para criar e gerenciar tarefas.

## Funcionalidades

- Criar tarefas com título, descrição e status
- Listar tarefas com filtro por status
- Buscar tarefa por ID
- Atualizar dados da tarefa
- Deletar tarefa
- Autenticação com usuário e senha
- Validação de dados de entrada

## Instalação

Instale JDK 21 e Maven.

## Configuração do Banco

Opção 1: Com Docker (Recomendado)

```bash
docker-compose up -d
```

Opção 2: SQL Server instalado localmente

Configure as variáveis de ambiente:

```bash
export DB_HOST=localhost
export DB_PORT=1433
export DB_NAME=todolist
export DB_USER=sa
export DB_PASSWORD=YourPassword
```

Veja DOCKER.md para mais detalhes sobre Docker.

## Executar

```bash
mvn clean package
mvn spring-boot:run
```

A aplicação estará em `http://localhost:8080`

## Usuários

| Username | Senha |
|----------|-------|
| admin | admin123 |
| user | user123 |

## Exemplos de Uso

Criar tarefa:
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Minha tarefa"}'
```

Listar tarefas:
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/tasks
```

Obter uma tarefa:
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/tasks/1
```

Atualizar tarefa:
```bash
curl -u admin:admin123 -X PUT http://localhost:8080/api/v1/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Nova tarefa","status":"DONE"}'
```

Deletar tarefa:
```bash
curl -u admin:admin123 -X DELETE http://localhost:8080/api/v1/tasks/1
```

Health check:
```bash
curl http://localhost:8080/actuator/health
```

## Status das Tarefas

- PENDING - Pendente
- IN_PROGRESS - Em andamento
- DONE - Concluída

## Testes

```bash
mvn test
```

## Tecnologias Usadas

- Java 21
- Spring Boot 3.3
- Spring Security
- Spring Data JPA
- SQL Server
- Flyway
- JUnit 5
- Mockito
- Docker
