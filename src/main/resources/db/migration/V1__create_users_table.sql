CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    enabled BIT NOT NULL DEFAULT 1
);

CREATE INDEX IX_users_username ON users(username);
