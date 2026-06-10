-- V0: Criar banco de dados todolist
IF NOT EXISTS (SELECT 1 FROM sys.databases WHERE name = 'todolist')
BEGIN
  CREATE DATABASE todolist;
END;