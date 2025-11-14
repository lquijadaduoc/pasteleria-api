#!/bin/bash

# Script para corregir el problema de user_id NULL en pedidos
# Este script se puede ejecutar en el VPS para aplicar la corrección inmediatamente

echo "🔧 Aplicando corrección para permitir pedidos sin usuario..."

# Ejecutar el script SQL en el contenedor de MySQL
docker exec -i panaderia-mysql mysql -u root -ppanaderia_root_2024 panaderia << 'EOF'
-- Modificar la columna user_id para permitir valores NULL
ALTER TABLE pedidos 
MODIFY COLUMN user_id BIGINT NULL;

-- Eliminar foreign key existente si existe
SET foreign_key_checks = 0;
ALTER TABLE pedidos 
DROP FOREIGN KEY IF EXISTS pedidos_ibfk_1;

-- Recrear foreign key que permita NULL
ALTER TABLE pedidos 
ADD CONSTRAINT fk_pedidos_user_id 
FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE SET NULL;

SET foreign_key_checks = 1;

-- Verificar que el cambio se aplicó
DESCRIBE pedidos;

SELECT 'Corrección aplicada exitosamente: user_id ahora permite NULL' as resultado;
EOF

echo "✅ Corrección aplicada. Ahora se pueden crear pedidos sin usuario registrado."