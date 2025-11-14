-- Script para corregir la tabla pedidos y permitir user_id NULL
-- Esto es necesario para pedidos anónimos (sin usuario registrado)

USE panaderia;

-- Modificar la columna user_id para permitir valores NULL
ALTER TABLE pedidos 
MODIFY COLUMN user_id BIGINT NULL;

-- Verificar que el cambio se aplicó correctamente
DESCRIBE pedidos;

-- Mensaje de confirmación
SELECT 'Tabla pedidos actualizada: user_id ahora permite valores NULL' as resultado;