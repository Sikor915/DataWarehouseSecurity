CREATE TABLE IF NOT EXISTS files (
    id SERIAL PRIMARY KEY,
    filename TEXT NOT NULL,
    filedata BYTEA NOT NULL,
    anonym_rules BYTEA,
    uploaded_at TIMESTAMP DEFAULT NOW()
);

-- Tabela użytkowników
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    trust_level INT DEFAULT 1
);
