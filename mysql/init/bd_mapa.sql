USE bd1;

CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) DEFAULT 'usuario',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE marcadores (
    id_marcador INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    coord_x FLOAT NOT NULL,
    coord_y FLOAT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuario_marcador (
    username VARCHAR(50) NOT NULL,
    id_marcador INT NOT NULL,
    PRIMARY KEY (username, id_marcador),
    FOREIGN KEY (username) REFERENCES usuarios(username) ON DELETE CASCADE,
    FOREIGN KEY (id_marcador) REFERENCES marcadores(id_marcador) ON DELETE CASCADE
);

INSERT INTO usuarios (username, email, password_hash, rol) VALUES ('admin', 'admin@mapa.com', 'admin123', 'admin');