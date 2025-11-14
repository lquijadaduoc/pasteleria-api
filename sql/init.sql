-- Script de inicialización para la base de datos de Panadería
-- Se ejecuta automáticamente al crear el contenedor MySQL

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS panaderia 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE panaderia;

-- Crear usuario y dar permisos
CREATE USER IF NOT EXISTS 'panaderia_user'@'%' IDENTIFIED BY 'panaderia_pass_2024';
GRANT ALL PRIVILEGES ON panaderia.* TO 'panaderia_user'@'%';
FLUSH PRIVILEGES;

-- ===============================
-- CREAR TODAS LAS TABLAS DESDE CERO
-- ===============================

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    fecha_nacimiento DATE,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    tipo_usuario ENUM('CLIENTE', 'ESTUDIANTE_DUOC', 'ADMIN') DEFAULT 'CLIENTE',
    rut VARCHAR(12),
    direccion VARCHAR(200),
    descuento_especial DECIMAL(5,2) DEFAULT 0.00,
    fecha_ultimo_pedido DATETIME,
    total_pedidos_realizados INT DEFAULT 0,
    monto_total_gastado DECIMAL(10,2) DEFAULT 0.00,
    INDEX idx_usuarios_email (email),
    INDEX idx_usuarios_tipo (tipo_usuario),
    INDEX idx_usuarios_activo (activo)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tabla de productos
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    imagen_url VARCHAR(300),
    tiempo_preparacion INT DEFAULT 0,
    ingredientes_principales TEXT,
    stock INT DEFAULT 0 NOT NULL,
    stock_minimo INT DEFAULT 5,
    INDEX idx_productos_categoria (categoria),
    INDEX idx_productos_disponible (disponible),
    INDEX idx_productos_codigo (codigo)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tabla de pedidos - IMPORTANTE: user_id puede ser NULL para pedidos anónimos
CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NULL, -- PERMITIR NULL para pedidos sin usuario registrado
    email_usuario VARCHAR(150),
    fecha_pedido DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega_solicitada DATE,
    fecha_entrega_real DATETIME,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento DECIMAL(10,2) DEFAULT 0.00,
    costo_envio DECIMAL(10,2) DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    estado ENUM('RECIBIDO', 'CONFIRMADO', 'EN_PREPARACION', 'LISTO_PARA_ENTREGA', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO') DEFAULT 'RECIBIDO',
    tipo_entrega ENUM('RETIRO_TIENDA', 'DELIVERY') DEFAULT 'RETIRO_TIENDA',
    observaciones VARCHAR(300),
    direccion_entrega VARCHAR(200),
    mensaje_especial VARCHAR(100),
    numero_boleta VARCHAR(20),
    codigo_seguimiento VARCHAR(50),
    notificacion_enviada BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_pedidos_usuario (user_id),
    INDEX idx_pedidos_estado (estado),
    INDEX idx_pedidos_fecha (fecha_pedido),
    INDEX idx_pedidos_numero (numero_pedido)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tabla de items de pedido
CREATE TABLE IF NOT EXISTS pedido_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    mensaje_personalizado VARCHAR(200),
    descuento_aplicado DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES productos(id) ON DELETE RESTRICT,
    INDEX idx_pedido_items_pedido (pedido_id),
    INDEX idx_pedido_items_producto (product_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

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

-- ===============================
-- INSERTAR DATOS INICIALES (SOLO SI NO EXISTEN)
-- ===============================

-- Productos iniciales (solo si la tabla está vacía)
INSERT INTO productos (codigo, nombre, descripcion, precio, categoria, disponible, stock, stock_minimo, imagen_url) 
SELECT * FROM (SELECT
    'T001' as codigo, 'Torta de Chocolate' as nombre, 'Deliciosa torta de chocolate con cobertura' as descripcion, 15000.00 as precio, 'TORTAS' as categoria, true as disponible, 20 as stock, 5 as stock_minimo, '/images/productos/torta-chocolate.jpg' as imagen_url
UNION ALL SELECT 'T002', 'Torta de Vainilla', 'Torta suave de vainilla con crema', 14000.00, 'TORTAS', true, 25, 5, '/images/productos/torta-vainilla.jpg'
UNION ALL SELECT 'T003', 'Torta Red Velvet', 'Torta red velvet con cream cheese', 18000.00, 'TORTAS', true, 15, 3, '/images/productos/torta-red-velvet.jpg'
UNION ALL SELECT 'P001', 'Pan Frances', 'Pan francés tradicional', 2500.00, 'PANES', true, 50, 10, '/images/productos/pan-frances.jpg'
UNION ALL SELECT 'P002', 'Pan Integral', 'Pan integral con semillas', 3000.00, 'PANES', true, 40, 8, '/images/productos/pan-integral.jpg'
UNION ALL SELECT 'K001', 'Kuchen de Manzana', 'Kuchen tradicional de manzana', 8000.00, 'KUCHENS', true, 30, 5, '/images/productos/kuchen-manzana.jpg'
UNION ALL SELECT 'K002', 'Kuchen de Frambuesa', 'Kuchen con frambuesas frescas', 9000.00, 'KUCHENS', true, 25, 5, '/images/productos/kuchen-frambuesa.jpg'
UNION ALL SELECT 'G001', 'Galletas de Chocolate', 'Galletas caseras de chocolate', 5000.00, 'GALLETAS', true, 60, 15, '/images/productos/galletas-chocolate.jpg'
UNION ALL SELECT 'G002', 'Galletas de Avena', 'Galletas saludables de avena', 4500.00, 'GALLETAS', true, 50, 12, '/images/productos/galletas-avena.jpg'
UNION ALL SELECT 'E001', 'Empanadas de Pino', 'Empanadas tradicionales chilenas', 2000.00, 'EMPANADAS', true, 40, 10, '/images/productos/empanada-pino.jpg'
) as tmp
WHERE NOT EXISTS (SELECT 1 FROM productos LIMIT 1);

-- Usuario administrador por defecto (solo si no existe)
INSERT INTO usuarios (nombre, apellido, email, tipo_usuario, activo, fecha_registro) 
SELECT 'Admin', 'Sistema', 'admin@panaderia.com', 'ADMIN', true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@panaderia.com');


-- Configuraciones adicionales para optimización
SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';
SET GLOBAL time_zone = '-03:00'; -- Horario de Chile

-- Verificar que las tablas se crearon correctamente
SELECT 'Verificando estructura de tablas...' as mensaje;
DESCRIBE pedidos;

-- Mensaje de confirmación
SELECT 'Base de datos panaderia inicializada correctamente con todas las tablas y datos iniciales' as mensaje;

SET FOREIGN_KEY_CHECKS = 1;