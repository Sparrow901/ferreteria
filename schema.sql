-- ============================================================
-- FERRETERÍA - Base de Datos
-- Proyecto Integrador Tercer Semestre
-- ============================================================

CREATE DATABASE IF NOT EXISTS ferreteria CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ferreteria;

-- Tabla de usuarios del sistema
CREATE TABLE IF NOT EXISTS usuario (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    nombre     VARCHAR(100) NOT NULL,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    rol        ENUM('ADMIN','VENDEDOR') NOT NULL,
    activo     BOOLEAN DEFAULT TRUE
);

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS cliente (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    nombre     VARCHAR(100) NOT NULL,
    contacto   VARCHAR(50),
    direccion  VARCHAR(200)
);

-- Tabla de productos / inventario
CREATE TABLE IF NOT EXISTS producto (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    numero_serie  VARCHAR(50) NOT NULL UNIQUE,
    nombre        VARCHAR(100) NOT NULL,
    precio        DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    stock         INT NOT NULL DEFAULT 0,
    stock_minimo  INT NOT NULL DEFAULT 5
);

-- Tabla de ventas
CREATE TABLE IF NOT EXISTS venta (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    fecha       DATETIME DEFAULT CURRENT_TIMESTAMP,
    total       DECIMAL(10,2) NOT NULL,
    cliente_id  INT,
    usuario_id  INT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla de detalle por venta
CREATE TABLE IF NOT EXISTS detalle_venta (
    id               INT PRIMARY KEY AUTO_INCREMENT,
    venta_id         INT NOT NULL,
    producto_id      INT NOT NULL,
    cantidad         INT NOT NULL CHECK (cantidad > 0),
    precio_unitario  DECIMAL(10,2) NOT NULL,
    descuento        DECIMAL(10,2) DEFAULT 0.00,
    subtotal         DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venta_id)    REFERENCES venta(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Trigger: actualizar stock al registrar una venta
DELIMITER $$
CREATE TRIGGER IF NOT EXISTS actualizar_stock
AFTER INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    UPDATE producto SET stock = stock - NEW.cantidad WHERE id = NEW.producto_id;
END$$
DELIMITER ;

-- Datos iniciales de prueba
INSERT IGNORE INTO usuario (nombre, username, password, rol) VALUES
    ('Administrador General', 'admin',    'admin123',    'ADMIN'),
    ('Vendedor Demo',         'vendedor', 'vendedor123', 'VENDEDOR');

INSERT IGNORE INTO cliente (nombre, contacto, direccion) VALUES
    ('Cliente Mostrador', '0000000000', 'Sin dirección');

INSERT IGNORE INTO producto (numero_serie, nombre, precio, stock, stock_minimo) VALUES
    ('MART-001', 'Martillo 16oz',       25000, 15, 5),
    ('DEST-002', 'Destornillador Plano', 8500,  3, 5),
    ('TABL-003', 'Tablilla MDF 1mt',    35000, 20, 8),
    ('TORN-004', 'Tornillos x100',       5000, 50, 10),
    ('PINT-005', 'Pintura Blanca 1gl',  48000,  2, 5);
