-- Script para criar o banco de dados todolist e mover as tabelas
-- Execute este script conectado ao SQL Server como SA

-- 1. Criar o banco de dados todolist
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'todolist')
BEGIN
    CREATE DATABASE todolist;
    PRINT 'Database todolist criado com sucesso!';
END
ELSE
BEGIN
    PRINT 'Database todolist já existe!';
END

GO

-- 2. Usar o novo banco
USE todolist;

GO

-- 3. Se as tabelas existirem no master, mover para todolist
-- (Executar estas próximas linhas SOMENTE se precisar mover dados do master)

-- Criar as tabelas no novo banco (Flyway fará isso automaticamente na próxima execução)
-- Ou você pode criar manualmente:

CREATE TABLE IF NOT EXISTS users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    enabled BIT NOT NULL DEFAULT 1
);

CREATE INDEX IX_users_username ON users(username);

GO

CREATE TABLE IF NOT EXISTS tasks (
    id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(1000) NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME2 NOT NULL
);

CREATE INDEX IX_tasks_status ON tasks(status);

GO

-- 4. Inserir dados iniciais
INSERT INTO users (username, password, role, enabled)
VALUES ('admin', '$2a$10$0AgdAoG8Hu0v9y1O.2lCrO6ucN2qPphnGhDmYAGtC5PiLvt2A8CQy', 'ROLE_ADMIN', 1);

INSERT INTO users (username, password, role, enabled)
VALUES ('user', '$2a$10$q6k.3auEx9Ef3/pQ5TIuPOnj6WDX8t0wy2dz9XNm7Mvd094bg8LWK', 'ROLE_USER', 1);

PRINT 'Database todolist configurado com sucesso!';
PRINT 'Usuários iniciais criados: admin e user';

GO

-- 5. (OPCIONAL) Se precisar limpar o master, execute:
-- USE master;
-- DROP TABLE IF EXISTS dbo.tasks;
-- DROP TABLE IF EXISTS dbo.users;
