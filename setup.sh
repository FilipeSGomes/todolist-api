#!/bin/bash

echo "🚀 Iniciando setup do TodoList API..."

# 1. Parar containers anteriores
echo "1️⃣  Parando containers anteriores..."
docker-compose down 2>/dev/null || true
sleep 2

# 2. Remover volume antigo para começar do zero
echo "2️⃣  Removendo dados antigos..."
docker volume rm todolist-api_sqlserver_data 2>/dev/null || true

# 3. Iniciar containers
echo "3️⃣  Iniciando SQL Server..."
docker-compose up -d
sleep 15

# 4. Criar banco de dados e tabelas
echo "4️⃣  Criando banco de dados todolist..."
docker exec todolist-sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'YourPassword123!' << EOF
CREATE DATABASE todolist;
GO
USE todolist;
GO
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    enabled BIT NOT NULL DEFAULT 1
);
GO
CREATE INDEX IX_users_username ON users(username);
GO
CREATE TABLE tasks (
    id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(1000) NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME2 NOT NULL
);
GO
CREATE INDEX IX_tasks_status ON tasks(status);
GO
INSERT INTO users (username, password, role, enabled)
VALUES ('admin', '\$2a\$10\$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy8N56m', 'ROLE_ADMIN', 1);
GO
INSERT INTO users (username, password, role, enabled)
VALUES ('user', '\$2a\$10\$wYDCdPZwqVcfJSGgOJ3vJ.0PwXPCXYWp8qhPc2r3aIL4.W8bv8gm2', 'ROLE_USER', 1);
GO
EOF

# 5. Compilar aplicação
echo "5️⃣  Compilando aplicação..."
mvn clean package -DskipTests > /dev/null 2>&1

# 6. Iniciar aplicação
echo "6️⃣  Iniciando aplicação Spring Boot..."
java -jar target/todolist-api-1.0.0.jar > /tmp/app.log 2>&1 &
sleep 8

# 7. Verificar saúde
echo "7️⃣  Verificando saúde da aplicação..."
HEALTH=$(curl -s http://localhost:8080/actuator/health | grep -o '"status":"[^"]*"')

if [[ "$HEALTH" == *"UP"* ]]; then
    echo "✅ Sucesso! Aplicação está rodando em http://localhost:8080"
    echo "✅ Banco de dados: localhost,1433 | Database: todolist | User: sa"
    echo ""
    echo "📝 Usuários de teste:"
    echo "  - admin / admin123 (ROLE_ADMIN)"
    echo "  - user / user123 (ROLE_USER)"
else
    echo "❌ Erro ao iniciar aplicação"
    echo "📋 Verificar logs em /tmp/app.log"
fi
