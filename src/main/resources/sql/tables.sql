CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);

CREATE TABLE user_table (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE user_roles (
    usuario_id INTEGER REFERENCES users(id),
    rol_id INTEGER REFERENCES roles(id),
    PRIMARY KEY (usuario_id, rol_id)
);

CREATE TABLE real_estate_projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    description TEXT
);

CREATE TABLE reports (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100),
    content TEXT,
    project_id INTEGER REFERENCES real_estate_projects(id),
    user_id INTEGER REFERENCES users(id)
);

-- Inserta los roles por defecto
INSERT INTO roles (nombre) VALUES ('USER');
INSERT INTO roles (nombre) VALUES ('ADMIN');


-- Inserta un usuario con rol admin utilizando la encriptacion de el package utils bajo la contraseña 100 
BEGIN;


INSERT INTO user_table (nombre, apellido, username, email, password)
VALUES ('zzz', 'zzz', 'zzz', 'zzz@zzz.com', '$2a$10$TVcp./6xSUedOZnSEhKyRukZdyGXPNpmxCsNM2m5zdWUqUGgOv.1i')
RETURNING id;


INSERT INTO user_roles (usuario_id, rol_id)
VALUES (currval(pg_get_serial_sequence('user_table','id')), 2);

COMMIT;

