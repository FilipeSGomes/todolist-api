#!/bin/bash
set -e

# Aguardar SQL Server ficar pronto
echo "Aguardando SQL Server ficar pronto..."
for i in {1..50}; do
    /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'YourPassword123!' -Q "SELECT 1" && break
    echo "Tentativa $i: Aguardando SQL Server..."
    sleep 2
done

echo "SQL Server está pronto! Criando banco de dados todolist..."

# Criar banco de dados e tabelas
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'YourPassword123!' << EOF
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

echo "✅ Banco de dados todolist criado com sucesso!"
