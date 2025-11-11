-- Script de inicialización para la base de datos de Panadería
-- Se ejecuta automáticamente al crear el contenedor MySQL

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS panaderia_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE panaderia_db;

-- Crear usuario y dar permisos
CREATE USER IF NOT EXISTS 'panaderia_user'@'%' IDENTIFIED BY 'PanaderiaUser2024!';
GRANT ALL PRIVILEGES ON panaderia_db.* TO 'panaderia_user'@'%';
FLUSH PRIVILEGES;

-- Agregar columnas de stock a la tabla productos (si no existen)
ALTER TABLE productos 
ADD COLUMN IF NOT EXISTS stock INT DEFAULT 0 NOT NULL,
ADD COLUMN IF NOT EXISTS stock_minimo INT DEFAULT 5;

-- Crear tabla de ventas
CREATE TABLE IF NOT EXISTS ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_venta VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT,
    email_cliente VARCHAR(150),
    nombre_cliente VARCHAR(200),
    fecha_venta DATETIME NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,
    estado ENUM('COMPLETADA', 'CANCELADA', 'PENDIENTE') DEFAULT 'COMPLETADA',
    metodo_pago ENUM('EFECTIVO', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'TRANSFERENCIA') DEFAULT 'EFECTIVO',
    observaciones TEXT,
    FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_ventas_fecha (fecha_venta),
    INDEX idx_ventas_estado (estado),
    INDEX idx_ventas_cliente (email_cliente)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear tabla de items de venta
CREATE TABLE IF NOT EXISTS venta_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    mensaje_personalizado VARCHAR(200),
    descuento_aplicado DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES productos(id) ON DELETE RESTRICT,
    INDEX idx_venta_items_venta (venta_id),
    INDEX idx_venta_items_producto (product_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Actualizar stock inicial de productos existentes (establecer stock por defecto)
UPDATE productos SET stock = 50, stock_minimo = 5 WHERE stock IS NULL OR stock = 0;

-- Configuraciones adicionales para optimización
SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';
SET GLOBAL time_zone = '-03:00'; -- Horario de Chile

-- Mensaje de confirmación
SELECT 'Base de datos panaderia_db inicializada correctamente con tablas de ventas y stock' as mensaje;

SET FOREIGN_KEY_CHECKS = 1;