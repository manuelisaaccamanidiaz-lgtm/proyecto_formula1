CREATE DATABASE IF NOT EXISTS formula1;
USE formula1;

CREATE TABLE team (
    id_equipo INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    pais VARCHAR(60)
);

CREATE TABLE vehicle (
    id_vehiculo INT PRIMARY KEY AUTO_INCREMENT,
    motor VARCHAR(50),
    modelo VARCHAR(30),
    aceleracion DOUBLE,
    velocidad_maxima INT,
    id_equipo INT,
    
    FOREIGN KEY (id_equipo) REFERENCES team(id_equipo)
        ON DELETE RESTRICT
);

CREATE TABLE pilot (
    id_piloto INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    rol VARCHAR(50),
    id_equipo INT,
    id_vehiculo INT UNIQUE,
    
    FOREIGN KEY (id_equipo) REFERENCES team(id_equipo)
        ON DELETE RESTRICT,
    FOREIGN KEY (id_vehiculo) REFERENCES vehicle(id_vehiculo)
        ON DELETE RESTRICT
);

CREATE TABLE circuit (
    id_circuito INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50),
    longitud_km DOUBLE,
    descripcion TEXT,
    vueltas INT
);

CREATE TABLE result (
    id_result INT PRIMARY KEY AUTO_INCREMENT,
    tiempo DOUBLE,
    id_circuito INT,
    id_vehiculo INT,
    
    FOREIGN KEY (id_circuito) REFERENCES circuit(id_circuito)
        ON DELETE RESTRICT,
    FOREIGN KEY (id_vehiculo) REFERENCES vehicle(id_vehiculo)
        ON DELETE RESTRICT
);

CREATE TABLE rendimiento_vehiculo (
    id_rendimiento INT PRIMARY KEY AUTO_INCREMENT,
    id_vehiculo INT NOT NULL,
    modo ENUM('normal', 'agresiva', 'ahorro') NOT NULL,
    velocidad_promedio_kmh INT,
    consumo_seco DOUBLE,
    consumo_lluvioso DOUBLE,
    consumo_extremo DOUBLE,
    desgaste_seco DOUBLE,
    desgaste_lluvioso DOUBLE,
    desgaste_extremo DOUBLE,
    FOREIGN KEY (id_vehiculo) REFERENCES vehicle(id_vehiculo)
        ON DELETE CASCADE
);