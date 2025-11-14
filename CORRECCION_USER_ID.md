# Corrección del Error user_id en VPS

## Problema
Error: `Column 'user_id' cannot be null` al crear pedidos sin usuario registrado.

## Solución Rápida (Ejecutar en el VPS)

### 1. Conectarse al VPS:
```bash
ssh root@168.197.50.14
```

### 2. Ejecutar corrección inmediata:
```bash
# Aplicar corrección a la base de datos existente
docker exec -i panaderia-mysql mysql -u root -ppanaderia_root_2024 panaderia << 'EOF'
ALTER TABLE pedidos MODIFY COLUMN user_id BIGINT NULL;
SET foreign_key_checks = 0;
ALTER TABLE pedidos DROP FOREIGN KEY IF EXISTS pedidos_ibfk_1;
ALTER TABLE pedidos ADD CONSTRAINT fk_pedidos_user_id FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE SET NULL;
SET foreign_key_checks = 1;
SELECT 'Corrección aplicada exitosamente' as resultado;
EOF
```

### 3. Verificar la corrección:
```bash
docker exec -i panaderia-mysql mysql -u root -ppanaderia_root_2024 panaderia -e "DESCRIBE pedidos;"
```

## Despliegue Completo (Opcional)

Si prefieres redesplegar completamente con las correcciones incluidas:

### 1. Subir nuevo paquete:
```bash
scp panaderia-api-deploy.zip root@168.197.50.14:/root/
```

### 2. En el VPS:
```bash
cd /root
unzip -o panaderia-api-deploy.zip
chmod +x deploy.sh
./deploy.sh
```

## Verificación Final

Probar creación de pedido sin usuario:
```bash
curl -X POST http://168.197.50.14:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "emailUsuario": "test@example.com",
    "observaciones": "Pedido de prueba sin usuario registrado",
    "items": [
      {
        "productId": 1,
        "cantidad": 1,
        "mensajePersonalizado": "Torta de prueba"
      }
    ]
  }'
```

## Archivos Incluidos en la Corrección

- ✅ `sql/init.sql` - Script actualizado con corrección
- ✅ `sql/fix_pedidos_user_id.sql` - Script específico para la corrección  
- ✅ `fix-user-id-null.sh` - Script de corrección rápida para VPS
- ✅ Código del controlador ya preparado para pedidos sin usuario

## Estado Después de la Corrección

- ✅ Pedidos con usuario registrado: Funcionan normalmente
- ✅ Pedidos sin usuario (anónimos): Ahora funcionan correctamente  
- ✅ user_id puede ser NULL en la base de datos
- ✅ Relaciones de foreign key actualizadas correctamente