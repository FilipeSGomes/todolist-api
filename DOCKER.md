# Docker Setup

Como usar Docker Compose para rodar o SQL Server localmente.

## Pré-requisitos

- Docker Desktop instalado
- Docker Compose (incluído no Docker Desktop)

## Como Usar

### 1. Iniciar o SQL Server

Na raiz do projeto, execute:

```bash
docker-compose up -d
```

Isso vai iniciar o SQL Server Express 2022 na porta 1433.

### 2. Verificar Status

```bash
docker-compose ps
```

Você deve ver algo como:

```
NAME                 STATUS
todolist-sqlserver   Up (healthy)
```

### 3. Variáveis de Ambiente

O arquivo `.env` já contém as credenciais configuradas:

```
DB_HOST=localhost
DB_PORT=1433
DB_NAME=todolist
DB_USER=sa
DB_PASSWORD=YourPassword123!
```

### 4. Executar a Aplicação

```bash
mvn spring-boot:run
```

As migrations Flyway vão executar automaticamente na primeira vez.

### 5. Testar

```bash
curl -u admin:admin123 http://localhost:8080/api/v1/tasks
```

## Comandos Úteis

Ver logs do banco:

```bash
docker-compose logs sqlserver
```

Parar o container:

```bash
docker-compose down
```

Parar e remover volumes (dados serão perdidos):

```bash
docker-compose down -v
```

Conectar ao banco via SQL:

```bash
docker exec -it todolist-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P 'YourPassword123!'
```

## Troubleshooting

Se receber erro de conexão, aguarde alguns segundos para o SQL Server estar pronto. O healthcheck valida a conexão automaticamente.

Para resetar tudo:

```bash
docker-compose down -v
docker-compose up -d
```
